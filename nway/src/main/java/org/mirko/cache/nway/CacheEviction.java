package org.mirko.cache.nway;

import java.util.List;

/**
 * <p>A cache eviction algorithm is a way of deciding which element to evict when the cache is full.
 * When the store gets full, elements are evicted.</p>
 * <p>Eviction algorithm implementation is not removing entries from the {@code block} but only mark their
 * {@code status} to DELETE</p>
 * <p>The default implementation is {@link org.mirko.cache.nway.algorithm.LRUAlgorithm} but also
 * {@link org.mirko.cache.nway.algorithm.LRUExpiredAlgorithm} and
 * {@link org.mirko.cache.nway.algorithm.MRUAlgorithm} are provided with the package</p>
 * <p>In order to provide your own algorithm you have to implement this interface and give to the builder via
 * {@link org.mirko.cache.nway.NWayCacheBuilder#customEviction(CacheEviction)}</p>
 * <p/>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface CacheEviction<Key, Value> {

    /**
     * Eviction algorithm implementation.<br/>
     * {@code block} is an immutable list because the eviction doesn't remove the entries but it marks it to {@code DELETED}
     *
     * @param block immutable list that represent the memory block
     */
    void eviction(List<CacheEntry<Key, Value>> block);
}
