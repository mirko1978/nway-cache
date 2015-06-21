package org.mirko.cache.nway;

/**
 * Define the caching loading in case the cache has a miss.
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface CacheLoader<Key, Value> {
    /**
     * Load a value given a key. In case of exception a {@link org.mirko.cache.nway.CacheLoaderException} will be
     * throw from the {@link org.mirko.cache.nway.Cache#get(Object)} method that wrap the exception
     *
     * @param key the key that doesn't have an associate value
     * @return the value loaded from somewhere
     * @throws java.lang.Exception if something wron happen
     */
    Value load(Key key) throws Exception;
}
