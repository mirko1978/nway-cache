package org.mirko.cache.nway;

/**
 * Describe a cache entry in order to be used during the eviction algorithm.
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface CacheEntry<Key, Value> {
    /**
     * Creation timestamp expressed in millisecond from {@link System#currentTimeMillis()}
     *
     * @return the difference, measured in milliseconds, between
     * the entry creation time and midnight, January 1, 1970 UTC
     */
    long getCreationTime();

    /**
     * The entry key
     *
     * @return key
     */
    Key getKey();

    /**
     * The entry value
     *
     * @return value
     */
    Value getValue();

    /**
     * Access timestamp expressed in millisecond from {@link System#currentTimeMillis()}
     *
     * @return the difference, measured in milliseconds, between
     * the last entry accessed time and midnight, January 1, 1970 UTC
     */
    long getAccessTime();

    /**
     * Status of the current entry {@link CacheEntryStatus}
     *
     * @return the status {@code ACTIVE or DELETED}
     */
    CacheEntryStatus getStatus();

    /**
     * Set the entry status {@link CacheEntryStatus}
     *
     * @param status the status {@code ACTIVE or DELETED}
     */
    void setStatus(CacheEntryStatus status);
}
