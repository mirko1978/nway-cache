package org.mirko.cache.nway.algorithm;

import com.google.common.base.Preconditions;
import org.mirko.cache.nway.CacheEntry;
import org.mirko.cache.nway.CacheEntryStatus;
import org.mirko.cache.nway.CacheEviction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of a basic <a href="http://en.wikipedia.org/wiki/Cache_algorithms#LRU">MRU</a> algorithm.<br/>
 * Mark for deletion {@link MRUAlgorithm#entriesToDelete} youngest entries in the cache. <br/>
 * The cache stores the elements following the creation order (the older are first), then the deletion happen from the
 * tail of the block.
 * <br/><br/>
 * {@link MRUAlgorithm#entriesToDelete} Cannot be < 1 otherwise an {@link java.lang.IllegalArgumentException} is raised
 * when the method {@link MRUAlgorithm#eviction(java.util.List)} is called.
 *
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 * @author Mirko Bernardoni
 * @since 1.0
 * @version 1.0
 */
public class MRUAlgorithm<Key, Value> implements CacheEviction<Key, Value> {

    private int entriesToDelete;
    private static final Logger LOG = LoggerFactory.getLogger(MRUAlgorithm.class);
    /**
     * Mark for deletion the youngest <code>entriesToDelete</code> entries
     *
     * @param block the block to analise
     * @throws java.lang.IllegalArgumentException in case <code>entriesToDelete</code> is < 1
     */
    @Override
    public void eviction(List<CacheEntry<Key, Value>> block) {
        Preconditions.checkArgument(entriesToDelete > 0, "At least 1 entry has to be deleted");
        if (block.isEmpty()) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("MRU called for empty block");
            }
            return;
        }
        ListIterator<CacheEntry<Key, Value>> iterator = block.listIterator(block.size());
        int i = 0;
        while (iterator.hasPrevious() && i < entriesToDelete) {
            CacheEntry<Key, Value> entry = iterator.previous();
            entry.setStatus(CacheEntryStatus.DELETED);
            if(LOG.isDebugEnabled()) {
                LOG.debug("MRU mark for deletion {}", entry);
            }
            i++;
        }
    }
    /**
     * Define how many younger entries will be deleted for each eviction.
     *
     * @param entriesToDelete entries to the delete. Must be > 1
     * @throws java.lang.IllegalArgumentException in case <code>entriesToDelete</code> is < 1
     */
    public void setEntriesToDelete(int entriesToDelete) {
        Preconditions.checkArgument(entriesToDelete > 0, "At least 1 entry has to be deleted");
        this.entriesToDelete = entriesToDelete;
    }
}