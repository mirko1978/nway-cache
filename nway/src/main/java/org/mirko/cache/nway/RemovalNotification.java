package org.mirko.cache.nway;

import com.google.common.base.MoreObjects;

/**
 * Snapshot copy of an entry during the removal event.
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class RemovalNotification<Key, Value> extends CacheNotification<Key, Value> {
    private final RemovalCause cause;

    /**
     * Create a new instance of RemovalNotification by coping the entry value
     * @param entry entry removed from the cache
     * @param cause cause of remmoval
     */
    /*package*/ RemovalNotification(CacheEntry<Key, Value> entry, RemovalCause cause) {
        super(entry);
        this.cause = cause;
    }

    /**
     * Get the cause of the removal
     * @return cause of the removal
     */
    public RemovalCause getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Cause", cause)
                .add("Key", getKey())
                .add("Value", getValue())
                .add("CreationTime", getCreationTime())
                .add("AccessTime", getAccessTime())
                .toString();
    }
}
