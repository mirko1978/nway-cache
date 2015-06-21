package org.mirko.cache.nway;

/**
 * listener that is trigger when a entry is removed from the cache
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface RemovalListener<Key, Value> {
    /**
     * it is called when a entry is removed from the cache
     * @param notification notification
     */
    void onRemoval(RemovalNotification<Key, Value> notification);

}
