package org.mirko.cache.nway;

import com.google.common.base.MoreObjects;

/**
 * Snapshot copy of an entry during an event.
 * <p/>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class CacheNotification<Key, Value> {
    private final Key key;
    private final Value value;
    private final long accessTime;
    private final long creationTime;

    /**
     * Create a new instance of CacheNotification by coping the entry value.
     *
     * @param entry entry to copy from
     */
    /*package*/ CacheNotification(CacheEntry<Key, Value> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
        this.accessTime = entry.getAccessTime();
        this.creationTime = entry.getCreationTime();
    }

    /**
     * The entry key
     *
     * @return key
     */
    public Key getKey() {
        return key;
    }

    /**
     * The entry value
     *
     * @return value
     */
    public Value getValue() {
        return value;
    }

    /**
     * Access timestamp expressed in millisecond from {@link System#currentTimeMillis()}
     *
     * @return the difference, measured in milliseconds, between
     * the last entry accessed time and midnight, January 1, 1970 UTC
     */
    public long getAccessTime() {
        return accessTime;
    }

    /**
     * Creation timestamp expressed in millisecond from {@link System#currentTimeMillis()}
     *
     * @return the difference, measured in milliseconds, between
     * the entry creation time and midnight, January 1, 1970 UTC
     */
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Key", key)
                .add("Value", value)
                .add("CreationTime", creationTime)
                .add("AccessTime", accessTime)
                .toString();
    }
}
