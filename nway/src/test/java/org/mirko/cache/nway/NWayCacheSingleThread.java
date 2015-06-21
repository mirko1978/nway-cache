package org.mirko.cache.nway;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>This class is used only by the performance test in order to give an indication about the maximum performance
 * achievable with your computer.</p>
 * <p><b>DO NOT USE IN PRODUCTION!!!!</b></p>
 * <p><b>NOT THREAD SAFE AND DOESN'T FIRE ANY EVENTS!!!!</b></p>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
/*package*/ class NWayCacheSingleThread<Key, Value> extends AbstractCache<Key, Value> {
    private final int numBuckets;
    private final int nWay;
    private final List<CacheBag<Key, Value>> buckets;

    /*package*/ NWayCacheSingleThread(int numBuckets, int nWay) {
        this.numBuckets = numBuckets;
        this.nWay = nWay;
        List<CacheBag<Key, Value>> buckets = new ArrayList<>(numBuckets);
        for (int i = 0; i < numBuckets; i++) {
            CacheBag<Key, Value> blocks = new CacheBag<>(new LinkedList<>());
            buckets.add(blocks);
        }
        this.buckets = ImmutableList.copyOf(buckets);
    }

    @Override
    public void put(Key key, Value value) {
        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);
        if (result.isEmpty()) {
            // New entry
            addEntry(bag, key, value);
        } else {
            // substitution
            // Delete everything and reload
            removeEntry(result);
            addEntry(bag, key, value);
        }
    }

    @Override
    public Value get(Key key) throws CacheLoaderException {
        CacheLoader<Key, Value> cacheLoader = getCacheLoader();
        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);

        if (result == null) {
            throw new RuntimeException("Unexpected null for get key " + key);
        }
        Value value;
        if (result.isEmpty()) {
            // miss: callback for adding
            try {
                value = cacheLoader.load(key);
            } catch (Exception e) {
                throw new CacheLoaderException("Error in load", e);
            }
            addEntry(bag, key, value);
        } else if (result.size() > 1) {
            // Multiple value for the same key => Cache error
            // Delete everityng and reload
            removeEntry(result);

            try {
                value = cacheLoader.load(key);
            } catch (Exception e) {
                throw new CacheLoaderException("Error in load", e);
            }
            addEntry(bag, key, value);
        } else {
            // 1 value found in the cache
            CacheEntry<Key, Value> entry = result.get(0);
            value = getAndUpdateTime(entry);
        }
        return value;
    }

    @Override
    public void remove(Key key) {
        CacheBag<Key, Value> bag = findBag(key);
        List<CacheEntry<Key, Value>> result = findEntry(key, bag);
        removeEntry(result);
    }

    private void removeEntry(List<CacheEntry<Key, Value>> entitiesToRemove) {
        if (entitiesToRemove == null) {
            return;
        }
        entitiesToRemove.forEach(e -> e.setStatus(CacheEntryStatus.DELETED));
    }

    private void addEntry(CacheBag<Key, Value> bag, Key key, Value value) {
        CacheEntryImpl<Key, Value> entry = new CacheEntryImpl<>();
        entry.setAccessTime(System.currentTimeMillis());
        entry.setValue(value);
        entry.setKey(key);
        entry.setStatus(CacheEntryStatus.ACTIVE);
        List<CacheEntry<Key, Value>> block = bag.getBlock();

        if (block.size() >= nWay) {
            // End of bag space -> need to call eviction
            List<CacheEntry<Key, Value>> immutableBlocks;
            immutableBlocks = ImmutableList.copyOf(block);
            getEviction().eviction(immutableBlocks);

            Iterator<CacheEntry<Key, Value>> iterator = block.iterator();
            while (iterator.hasNext()) {
                CacheEntry<Key, Value> entryToDelete = iterator.next();
                if (entryToDelete.getStatus() == CacheEntryStatus.DELETED) {
                    iterator.remove();
                }
            }
        }
        // there is space then add in the end
        block.add(entry);
    }

    private Value getAndUpdateTime(CacheEntry<Key, Value> entry) {
        ((CacheEntryImpl) entry).setAccessTime(System.currentTimeMillis());
        return entry.getValue();
    }

    private CacheBag<Key, Value> findBag(Key key) {
        int bucket = key.hashCode() % numBuckets;
        return buckets.get(bucket);
    }

    private List<CacheEntry<Key, Value>> findEntry(Key key, CacheBag<Key, Value> bag) {
        return bag.getBlock()
                .stream()
                .filter(e -> e.getStatus() == CacheEntryStatus.ACTIVE && key.equals(e.getKey()))
                .collect(Collectors.toList());
    }
}
