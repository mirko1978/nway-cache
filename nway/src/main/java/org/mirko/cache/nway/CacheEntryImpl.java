package org.mirko.cache.nway;

import com.google.common.base.MoreObjects;

/**
 * Package accessible bean that implement {@link CacheEntry}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
/*package*/ class CacheEntryImpl<Key, Value> implements CacheEntry<Key, Value> {
    private final long creationTime;
    private Key key;
    private Value value;
    // multiple thread can change it
    private volatile long accessTime;
    // multiple thread can change it
    private volatile CacheEntryStatus status;

    /**
     * Create a new instance of CacheEntryImpl with {@link #creationTime} set to current time
     */
    /*package*/ CacheEntryImpl() {
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public Key getKey() {
        return key;
    }

    /*package*/ void setKey(Key key) {
        this.key = key;
    }

    @Override
    public Value getValue() {
        return value;
    }

    /*package*/ void setValue(Value value) {
        this.value = value;
    }

    @Override
    public long getAccessTime() {
        return accessTime;
    }

    /*package*/ void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    @Override
    public CacheEntryStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(CacheEntryStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheEntryImpl that = (CacheEntryImpl) o;

        if (!key.equals(that.key)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
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
