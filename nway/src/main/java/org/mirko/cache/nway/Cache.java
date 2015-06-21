package org.mirko.cache.nway;

/**
 * <p>A  mapping from keys to values. Cache entries are manually added using
 * {@link #get(Object)} or {@link #put(Object, Object)}, and are stored in the cache until
 * either evicted or manually invalidated by {@link #remove(Object)}.
 * <p/>
 * <p>Implementations of this interface are expected to be thread-safe, and can be safely accessed
 * by multiple concurrent threads.</p>
 * <p/>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public interface Cache<Key, Value> {
    /**
     * <p>Associates {@code value} with {@code key} in this cache. If the cache previously contained a
     * value associated with {@code key}, the old value is replaced by {@code value}.
     * </p>
     * <p>Prefer {@link #get(Object)} when using the conventional "if cached, return;
     * otherwise create, cache and return" pattern.</p>
     *
     * @param key   the key
     * @param value value to put in the cache if there is a cache miss
     */
    void put(Key key, Value value);

    /**
     * <p>Returns the value associated with {@code key} in this cache, obtaining that value from
     * {@code cacheLoader} if necessary. No observable state associated with this cache is modified
     * until loading completes. This method provides a simple substitute for the conventional
     * "if cached, return; otherwise create, cache and return" pattern.</p>
     *
     * @param key the key for retrieve the value
     * @return the value from the cache or loaded via {@link org.mirko.cache.nway.CacheLoader} if wasn't in cache
     * @throws CacheLoaderException if an error was thrown while loading the value
     */
    Value get(Key key) throws CacheLoaderException;

    /**
     * Discards any cached value for key {@code key}.
     *
     * @param key the key to remove
     */
    void remove(Key key);

    /**
     * Add a removal listener.
     *
     * @param removalListener listener that is trigger when a entry is removed from the cache
     */
    void addRemovalListener(RemovalListener<Key, Value> removalListener);

    /**
     * Remove a removal listener
     *
     * @param removalListener listener that is trigger when a entry is removed from the cache
     */
    void removeRemovalListener(RemovalListener<Key, Value> removalListener);

    /**
     * Add a cache miss listener
     *
     * @param missListener listener that is trigger when a entry is loaded via {@code cacheLoader}
     */
    void addMissListener(MissListener<Key> missListener);

    /**
     * Remove a cache miss listener
     *
     * @param missListener listener that is trigger when a entry is loaded via {@code cacheLoader}
     */
    void removeMissListener(MissListener<Key> missListener);

    /**
     * Add a cached listener
     *
     * @param cachedListener listener that is trigger when a entry is retrieved from the cache (no load occur)
     */
    void addCachedListener(CachedListener<Key, Value> cachedListener);

    /**
     * Remove a cached listener
     *
     * @param cachedListener listener that is trigger when a entry is retrieved from the cache (no load occur)
     */
    void removeCachedListener(CachedListener<Key, Value> cachedListener);

}
