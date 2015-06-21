package org.mirko.cache.nway.algorithm;

import com.google.common.base.Preconditions;
import org.mirko.cache.nway.CacheEntry;
import org.mirko.cache.nway.CacheEntryStatus;
import org.mirko.cache.nway.CacheEviction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the <a href="http://en.wikipedia.org/wiki/Cache_algorithms#LRU">LRU</a> algorithm based on the access time.<br/>
 * Mark for deletion all the entries that are not used for {@code expiration} milliseconds following the below calculation.<br/>
 * <pre>{@code Expiration Time = Entry Access time + expiration
 * if Expiration Time < Current time then
 *      mark for deletion current entry
 * }</pre>
 * The time is expressed in milliseconds. <br/>
 * In addition the algorithm guarantee at least one deletion. In case no entries are expiring is going to delete the one
 * that has older access time.
 * <p/>
 * <br/><br/>
 * {@link LRUExpiredAlgorithm#expiration} Cannot be < 1 otherwise an {@link java.lang.IllegalArgumentException} is raised
 * when the method {@link LRUExpiredAlgorithm#eviction(java.util.List)} is called.
 *
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 * @author Mirko Bernardoni
 * @since 1.0
 * @version 1.0
 */
public class LRUExpiredAlgorithm<Key, Value> implements CacheEviction<Key, Value> {
    private static final Logger LOG = LoggerFactory.getLogger(LRUExpiredAlgorithm.class);
    private long expiration;

    /**
     * Mark for deletion all the entries that are not used for {@code expiration} milliseconds.
     *
     * @param block the block to analise
     * @throws java.lang.IllegalArgumentException in case <code>expiration</code> is < 1
     */
    @Override
    public void eviction(List<CacheEntry<Key, Value>> block) {
        Preconditions.checkArgument(expiration > 0, "Expiration time not set");

        if (block.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("LRU Expired called for empty block");
            }
            return;
        }

        Iterator<CacheEntry<Key, Value>> iterator = block.iterator();
        long now = System.currentTimeMillis();
        boolean atLeasOneDeletion = false;
        long oldTime = Long.MIN_VALUE;
        CacheEntry<Key, Value> older = null;
        while (iterator.hasNext()) {
            CacheEntry<Key, Value> entry = iterator.next();
            long entryTime = entry.getAccessTime();
            long expireAt = entryTime + expiration;
            if (now > expireAt) {
                entry.setStatus(CacheEntryStatus.DELETED);
                atLeasOneDeletion = true;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("LRU Expired: Mark to delete {}", entry);
                }
            }
            // Find the oldest  entry
            if (!atLeasOneDeletion && oldTime < entryTime) {
                older = entry;
                oldTime = entryTime;
            }
        }
        // Nothing expired but remove the older used
        if (!atLeasOneDeletion) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No entry expired, mark to delete the oldest {}", older);
            }
            //noinspection ConstantConditions
            older.setStatus(CacheEntryStatus.DELETED);
        }
    }

    /**
     * Define the expiration time period used for calculate the expiration time.
     * <br/>See class docs
     *
     * @param expiration expiration time period in milliseconds. Cannot be < 1
     * @throws java.lang.IllegalArgumentException in case <code>expiration</code> is < 1
     */
    public void setExpiration(long expiration) {
        Preconditions.checkArgument(expiration > 0, "Expiration time not set");
        this.expiration = expiration;
    }
}