package org.mirko.cache.nway;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Package accessible abstract Cache class for creating a generic implementation of {@link Cache}.<br/>
 * Provides the public controls method implementation for the listeners (removal, miss, cached) and the package getter and setter for
 * {@link CacheLoader} and {@link CacheEviction}
 *
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 * @author Mirko Bernardoni
 * @since 1.0
 * @version 1.0
 */
/*package*/ abstract class AbstractCache<Key, Value> implements Cache<Key, Value> {
    /*package*/ final List<RemovalListener<Key, Value>> removalListeners = new ArrayList<>();
    /*package*/ final List<MissListener<Key>> missListeners = new ArrayList<>();
    /*package*/ final List<CachedListener<Key, Value>> cachedListeners = new ArrayList<>();
    private CacheLoader<Key, Value> cacheLoader;
    private CacheEviction<Key, Value> eviction;

    @Override
    public void addRemovalListener(RemovalListener<Key, Value> removalListener) {
        if (removalListener != null) {
            removalListeners.add(removalListener);
        }
    }

    @Override
    public void removeRemovalListener(RemovalListener<Key, Value> removalListener) {
        if (removalListener != null) {
            removalListeners.remove(removalListener);
        }
    }

    @Override
    public void addMissListener(MissListener<Key> missListener) {
        if (missListener != null) {
            missListeners.add(missListener);
        }
    }

    @Override
    public void removeMissListener(MissListener<Key> missListener) {
        if (missListener != null) {
            missListeners.remove(missListener);
        }
    }

    @Override
    public void addCachedListener(CachedListener<Key, Value> cachedListener) {
        if (cachedListener != null) {
            cachedListeners.add(cachedListener);
        }
    }

    @Override
    public void removeCachedListener(CachedListener<Key, Value> cachedListener) {
        if (cachedListener != null) {
            cachedListeners.remove(cachedListener);
        }
    }

    /**
     * Retrieve the current cacheLoader
     *
     * @return the cache loader implementation
     */
    /*package*/ CacheLoader<Key, Value> getCacheLoader() {
        return cacheLoader;
    }

    /**
     * Set the cache loader
     *
     * @param cacheLoader cache loader implementation
     */
    /*package*/ void setCacheLoader(CacheLoader<Key, Value> cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    /**
     * Retrieve the eviction algorithm implementation
     *
     * @return eviction algorithm
     */
    /*package*/ CacheEviction<Key, Value> getEviction() {
        return eviction;
    }

    /**
     * Set the eviction algorithm
     *
     * @param eviction eviction algorithm
     */
    /*package*/ void setEviction(CacheEviction<Key, Value> eviction) {
        this.eviction = eviction;
    }

    /**
     * Fire all the removal listeners for {@code entry} and {@code cause}
     *
     * @param entry entry that trigger the event
     * @param cause cause for the removal
     */
    protected void fireRemovalListener(CacheEntry<Key, Value> entry, RemovalCause cause) {
        Preconditions.checkNotNull(entry, "Null entry not allowed");
        Preconditions.checkNotNull(cause, "Null Removal cause not allowed");
        if (!removalListeners.isEmpty()) {
            RemovalNotification<Key, Value> removalNotification = new RemovalNotification<>(entry, cause);
            removalListeners.forEach(r -> r.onRemoval(removalNotification));
        }
    }

    /**
     * Fire all cached listeners for {@code entry}
     *
     * @param entry entry that trigger the event
     */
    protected void fireCachedListener(CacheEntry<Key, Value> entry) {
        Preconditions.checkNotNull(entry, "Null entry not allowed");
        if (!cachedListeners.isEmpty()) {
            CacheNotification<Key, Value> cachedNotification = new CacheNotification<>(entry);
            cachedListeners.forEach(c -> c.onCache(cachedNotification));
        }
    }

    /**
     * fire all miss cache listeners for {@code entry}
     *
     * @param key key not found in the cache
     */
    protected void fireMissListener(Key key) {
        Preconditions.checkNotNull(key, "Null key not allowed");
        if (!missListeners.isEmpty()) {
            missListeners.forEach(m -> m.onMiss(key));
        }
    }
}
