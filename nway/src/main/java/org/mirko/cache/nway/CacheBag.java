package org.mirko.cache.nway;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Package accessible bean that describe the cache block.<br/>
 * A block is the set where the cache entries are stored. In case of 2 way cache the bag has size 2.<br/>
 * In a cache system can be present more than one block</p>
 * <p>A brief cache introduction can be found <a href="http://csillustrated.berkeley.edu/PDFs/handouts/cache-3-associativity-handout.pdf">here</a></p>
 * <p>In this implementation the bag contains also the lock for managing the concurrent access to the block. </p>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
/*package*/ class CacheBag<Key, Value> {
    private final List<CacheEntry<Key, Value>> block;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /*package*/ CacheBag(List<CacheEntry<Key, Value>> block) {
        this.block = block;
    }

    /**
     * Retrieve the the memory block where the entries are stored
     *
     * @return the block
     */
    public List<CacheEntry<Key, Value>> getBlock() {
        return block;
    }

    /**
     * Retrieve the concurrency lock used per manage the access to the current block
     *
     * @return the lock
     */
    public ReadWriteLock getLock() {
        return lock;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Bag size", block.size())
                .toString();
    }
}
