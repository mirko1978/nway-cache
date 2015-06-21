package org.mirko.cache.nway;

/**
 * listener that is trigger when a entry is loaded via {@code cacheLoader}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface MissListener<Key> {
    /**
     * It is called when a entry is not inside the cache
     * @param key the not found key
     */
    void onMiss(Key key);
}
