package org.mirko.cache.nway.algorithm;

import com.google.common.base.Preconditions;
import org.mirko.cache.nway.CacheEntry;
import org.mirko.cache.nway.CacheEntryStatus;
import org.mirko.cache.nway.CacheEviction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of a basic <a href="http://en.wikipedia.org/wiki/Cache_algorithms#LRU">LRU</a> algorithm.<br/>
 * Mark for deletion {@link LRUAlgorithm#entriesToDelete} oldest entries in the cache. <br/>
 * The cache stores the elements following the creation order (the older are first), then the deletion happen from the
 * head of the block.
 * <br/><br/>
 * {@link LRUAlgorithm#entriesToDelete} Cannot be < 1 otherwise an {@link java.lang.IllegalArgumentException} is raised
 * when the method {@link LRUAlgorithm#eviction(java.util.List)} is called.
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 * @author Mirko Bernardoni
 * @since 1.0
 * @version 1.0
 */
public class LRUAlgorithm<Key, Value> implements CacheEviction<Key, Value> {

    private static final Logger LOG = LoggerFactory.getLogger(LRUAlgorithm.class);
    private int entriesToDelete;

    /**
     * Mark for deletion the oldest <code>entriesToDelete</code> entries
     *
     * @param block the block to analise
     * @throws java.lang.IllegalArgumentException in case <code>entriesToDelete</code> is < 1
     */
    @Override
    public void eviction(List<CacheEntry<Key, Value>> block) {
        Preconditions.checkArgument(entriesToDelete > 0, "At least 1 entry has to be deleted");
        if (block.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("LRU called for empty block");
            }
            return;
        }
        block.stream().limit(entriesToDelete).forEach(e -> e.setStatus(CacheEntryStatus.DELETED));
    }

    /**
     * Define how many older entries will be deleted for each eviction.
     *
     * @param entriesToDelete entries to the delete. Must be > 1
     * @throws java.lang.IllegalArgumentException in case <code>entriesToDelete</code> is < 1
     */
    public void setEntriesToDelete(int entriesToDelete) {
        Preconditions.checkArgument(entriesToDelete > 0, "At least 1 entry has to be deleted");
        this.entriesToDelete = entriesToDelete;
    }
}