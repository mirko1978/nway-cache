package org.mirko.cache.nway;

/**
 * Cause of removal of an entity from the cache
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public enum RemovalCause {
    /** a new value is provided then the old is replaced */
    REPLACED,
    /** user with a {@link org.mirko.cache.nway.Cache#put(Object, Object)} has replaced the old value*/
    USER,
    /** eviction algorithm has removed the entity */
    EVICTION
}
