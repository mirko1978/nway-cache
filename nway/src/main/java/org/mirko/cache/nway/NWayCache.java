package org.mirko.cache.nway;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Package accessible class that implement N-way, set-associative cache.</p>
 * <p>In this implementation the {@code block} is realized using a {@link LinkedList} structure in order to have
 * easy access to head and tail and low cost for walking from head to tail and vice versa</p>
 * <p>This class cannot be instantiated by any client without using {@link org.mirko.cache.nway.NWayCacheBuilder}</p>
 * <p>Before calling the eviction algorithm the current block is copied in a R/O list using a Read lock (for performance purpose).
 * It is possible that the block exceed the Nway size for a short amount of time (in case of a huge numbers of
 * concurrent threads on the same block). The size will be reduced as next step after running the eviction algorithm</p>
 * <p/>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
/*package*/ class NWayCache<Key, Value> extends AbstractCache<Key, Value> {
    private static final Logger LOG = LoggerFactory.getLogger(NWayCache.class);
    /**
     * Number of memory blocks (buckets)
     */
    private final int numBlocks;
    private final int nWay;
    private final int maxEntryPerBlock;
    private final List<CacheBag<Key, Value>> cacheBags;

    /**
     * Create a new instance of NWayCache with {@code numbBuckets} and {@code nWay}
     *
     * @param numBlocks        number of memory blocks to allocate
     * @param nWay             N-Way allowed (or size of the single block)
     * @param maxEntryPerBlock max block size before throw an OutOfMemory exception
     * @throws java.lang.IllegalStateException if maxEntryPerBlock < nWay
     */
    /*package*/ NWayCache(int numBlocks, int nWay, int maxEntryPerBlock) {
        Preconditions.checkState(maxEntryPerBlock >= nWay, "maxEntryPerBlock has to be major or equals to nWay");
        this.numBlocks = numBlocks;
        this.nWay = nWay;
        this.maxEntryPerBlock = maxEntryPerBlock;
        // Initiate the blocks.
        List<CacheBag<Key, Value>> cacheBags = new ArrayList<>(numBlocks);
        for (int i = 0; i < numBlocks; i++) {
            CacheBag<Key, Value> bag = new CacheBag<>(new LinkedList<>());
            cacheBags.add(bag);
        }
        this.cacheBags = ImmutableList.copyOf(cacheBags);
    }

    @Override
    public void put(Key key, Value value) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");

        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);
        if (result.isEmpty()) {
            // New entry
            addEntry(bag, key, value);
        } else {
            // substitution
            // Delete everything and reload
            markToDelete(result);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removed {} ", result);
            }
            // Add the new entry
            addEntry(bag, key, value);
            result.forEach(entry -> fireRemovalListener(entry, RemovalCause.REPLACED));
        }
    }

    @Override
    public Value get(Key key) throws CacheLoaderException {
        Preconditions.checkNotNull(key, "Key cannot be null");
        CacheLoader<Key, Value> cacheLoader = getCacheLoader();
        Preconditions.checkNotNull(cacheLoader, "CacheLoader cannot be null");

        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);

        Value value;
        if (result.isEmpty()) {
            // miss: callback for adding
            fireMissListener(key);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Key {} not in cache... loading", key);
            }
            try {
                value = cacheLoader.load(key);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Exception during the load key " + key, e);
                }
                throw new CacheLoaderException("Exception during the load for key " + key, e);
            }
            addEntry(bag, key, value);
        } else if (result.size() > 1) {
            // Multiple value for the same key => Cache error
            // Delete everything and reload the value for the current key
            if (LOG.isErrorEnabled()) {
                LOG.error("Cache problem: Multiple entries found for key {}. Deleting all and load the value", key);
            }
            markToDelete(result);

            fireMissListener(key);
            try {
                value = cacheLoader.load(key);
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Exception during the load key " + key, e);
                }
                throw new CacheLoaderException("Exception during the load for key " + key, e);
            }
            addEntry(bag, key, value);

            result.forEach(entry -> fireRemovalListener(entry, RemovalCause.REPLACED));
        } else {
            // value found in the cache
            CacheEntry<Key, Value> entry = result.get(0);
            value = getAndUpdateTime(entry);
            fireCachedListener(entry);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Value found in cache for key {}", key);
            }
        }
        return value;
    }

    @Override
    public void remove(Key key) {
        Preconditions.checkNotNull(key, "Key cannot be null");

        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);
        if (!result.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mark to delete key {}", key);
            }
            // Don't real delete, just mark for deletion
            markToDelete(result);
            result.forEach(entry -> fireRemovalListener(entry, RemovalCause.USER));
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("Key {} not found, no action taken", key);
        }
    }

    /**
     * Verify if an entry is loaded in the cache
     *
     * @param key key to check
     * @return true in case the entry is in the cache, otherwise false
     */
    /*package*/ boolean exist(Key key) {
        Preconditions.checkNotNull(key, "Key cannot be null");

        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);
        return result != null && !result.isEmpty();
    }

    /**
     * Mark a set of entries to read to delete (change the status)
     *
     * @param entitiesToRemove entries to mark
     */
    protected void markToDelete(List<CacheEntry<Key, Value>> entitiesToRemove) {
        if (entitiesToRemove == null || entitiesToRemove.isEmpty()) {
            return;
        }
        // no lock is necessary because status is volatile
        entitiesToRemove.forEach(e -> e.setStatus(CacheEntryStatus.DELETED));
    }

    /**
     * <p>Create and add a new entry to the cache from {@code key} and {@code value}</p>
     * <p>If the size of the block is >= to nWay then the eviction algorithm is called and the entries with status
     * {@code DELETED} are removed from the cache</p>
     *
     * @param bag   the bag that is going to contains the new entry
     * @param key   key
     * @param value value
     */
    protected void addEntry(CacheBag<Key, Value> bag, Key key, Value value) {
        Preconditions.checkNotNull(bag, "CacheBag cannot be null");
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        Preconditions.checkNotNull(getEviction(), "Eviction cannot be null");

        // New entry creation
        CacheEntryImpl<Key, Value> entry = new CacheEntryImpl<>();
        entry.setAccessTime(System.currentTimeMillis());
        entry.setValue(value);
        entry.setKey(key);
        entry.setStatus(CacheEntryStatus.ACTIVE);
        List<CacheEntry<Key, Value>> block = bag.getBlock();

        if (block.size() >= nWay) {
            // End of bag space -> need to call eviction
            if (LOG.isDebugEnabled()) {
                LOG.debug("Calling eviction size {} max {} because {}", block.size(), nWay, entry);
            }
            // copy the block in the immutable list
            List<CacheEntry<Key, Value>> immutableBlocks;
            bag.getLock().readLock().lock();
            try {
                immutableBlocks = ImmutableList.copyOf(block);
            } finally {
                bag.getLock().readLock().unlock();
            }
            // call evictions
            getEviction().eviction(immutableBlocks);
        }
        // there is space then add in the end
        bag.getLock().writeLock().lock();
        try {
            Iterator<CacheEntry<Key, Value>> iterator = block.iterator();
            while (iterator.hasNext()) {
                CacheEntry<Key, Value> entryToDelete = iterator.next();
                // remove all the entries wit status DELETED
                if (entryToDelete.getStatus() == CacheEntryStatus.DELETED ||
                // remove any duplicate key
                        key.equals(entryToDelete.getKey())) {
                    iterator.remove();
                    fireRemovalListener(entryToDelete, RemovalCause.EVICTION);
                }
            }
            // in the linked list the method add is equals to the method addLast
            block.add(entry);
        } finally {
            bag.getLock().writeLock().unlock();
        }

        if (block.size() >= maxEntryPerBlock) {
            // The eviction is not deleting enough!
            // Consider to change the eviction parameters
            throw new OutOfMemoryError("Eviction is not deleting enough entries. The block size is bigger than " + (nWay * 2));
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Entry {} added", entry);
        }
    }

    /**
     * Retrieve the value from an entry and update the access time
     *
     * @param entry entry
     * @return the value of the entry
     */
    protected Value getAndUpdateTime(CacheEntry<Key, Value> entry) {
        Preconditions.checkNotNull(entry, "CacheEntry cannot be null");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating access time for {}", entry);
        }
        // no lock is necessary because access time is volatile
        ((CacheEntryImpl) entry).setAccessTime(System.currentTimeMillis());
        return entry.getValue();

    }

    /**
     * Apply the hash algorithm for retrieve the bag from the key
     *
     * @param key key
     * @return bag assigned to the key
     */
    protected CacheBag<Key, Value> findBag(Key key) {
        Preconditions.checkNotNull(key, "Key cannot be null");

        int bagPosition = Math.abs(key.hashCode() % numBlocks);
        return cacheBags.get(bagPosition);
    }

    /**
     * Search for an entry in the current bag from the key.<br/>
     *
     * @param key key
     * @param bag bag
     * @return a list of entries that should be always one element long. If nothing is found the list is empty (not null)
     */
    protected List<CacheEntry<Key, Value>> findEntry(Key key, CacheBag<Key, Value> bag) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkNotNull(bag, "Bag cannot be null");
        bag.getLock().readLock().lock();
        try {
            return bag.getBlock()
                    .stream()
                    .filter(e -> e.getStatus() == CacheEntryStatus.ACTIVE && key.equals(e.getKey()))
                    .collect(Collectors.toList());
        } finally {
            bag.getLock().readLock().unlock();
        }
    }
}
