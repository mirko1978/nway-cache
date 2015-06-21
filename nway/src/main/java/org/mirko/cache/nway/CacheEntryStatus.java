package org.mirko.cache.nway;

/**
 * Define the two possible status of a cache entry: DELETED or ACTIVE
 * <p/>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public enum CacheEntryStatus {
    /**
     * the cache entry is valid
     */
    ACTIVE,
    /**
     * the cache entry will be deleted at the first eviction
     */
    DELETED
}
