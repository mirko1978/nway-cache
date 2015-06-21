package org.mirko.cache.nway;

/**
 * Exception occurred during the load of an entry in the cache
 * <p/>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class CacheLoaderException extends Exception {
    /**
     * Creates a new instance with the given detail message.
     */
    public CacheLoaderException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with the given detail message and cause.
     */
    public CacheLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
