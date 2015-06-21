package org.mirko.cache.nway;

/**
 * Listener that is trigger when a entry is retrieved from the cache (no load occur)
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface CachedListener<Key, Value> {
    /**
     * It is called when a entry is retrieved from the cache (hit)
     *
     * @param notification Entry related information
     */
    void onCache(CacheNotification<Key, Value> notification);
}
