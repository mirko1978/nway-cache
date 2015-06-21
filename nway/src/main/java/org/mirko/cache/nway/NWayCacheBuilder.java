package org.mirko.cache.nway;

import com.google.common.base.Preconditions;
import org.mirko.cache.nway.algorithm.LRUAlgorithm;
import org.mirko.cache.nway.algorithm.LRUExpiredAlgorithm;
import org.mirko.cache.nway.algorithm.MRUAlgorithm;

/**
 * <p>Builder for NWayCache. The builder is following the <b>convention over configuration</b> philosophy.</p>
 * <p>The defaults are:
 * <ul>
 * <li>{@code blocks} = 50</li>
 * <li>{@code nWay} = 5</li>
 * <li>{@code maxEntryPerBlock} = 10 (nWay *2)</li>
 * <li>{@code expiration} = 30 minutes (used only by LRUExpired eviction algorithm)</li>
 * <li>{@code entriesToDelete} = 2 (used only by LRU and MRU eviction algorithms)</li>
 * <li>{@code eviction} = LRUAlgorithm</li>
 * </ul>
 * </p>
 * <p>The NWayCache allocates a chunk of memory, subdivides this into memory blocks or buckets, each block containing N slots/items.<br/>
 * A block is the set where the cache entries are stored. In case of 2 way cache the block has size 2.<br/>
 * The numbers of blocks are denominated by {@code blocks} and the size of the blocks are determined by {@code nWay}</p>
 * <p>When the space inside a block is over then an eviction algorithm will be called for removing some entries.
 * The algorithm can be custom or one of the provided one.</p>
 * <p>If the block size is major than {@link #maxEntryPerBlock} an {@link java.lang.OutOfMemoryError} exception is throw.
 * This allow the flexibility in a high concurrent environment to oscillate the size of the block</p>
 * <p>The concurrency locks are used only when an entry is deleted or added to the block. In other words the lock is at block level.</p>
 * <p>A brief cache introduction can be found <a href="http://csillustrated.berkeley.edu/PDFs/handouts/cache-3-associativity-handout.pdf">here</a></p>
 * <p>Example for the default cache:<br/>
 * <pre>{@code
 * Cache<Integer, String> myCache = new NWayCacheBuilder<>()
 *      .build(key -> {return data from somewhere...});
 * }</pre>
 * Example with a MRU eviction algorithm:<br/>
 * <pre>{@code
 * Cache<Integer, String> myCache = new NWayCacheBuilder<>()
 *      .MRUEviction()
 *      .nWay(10)
 *      .build(key -> {return data from somewhere...});
 * }</pre>
 * Example with a custom eviction algorithm:<br/>
 * <pre>{@code
 * Cache<Integer, String> myCache = new NWayCacheBuilder<>()
 *      .customEviction(block -> {my custom eviction implemetation})
 *      .build(key -> {return data from somewhere...});
 * }</pre>
 * </p>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("JavaDoc")
public class NWayCacheBuilder<Key, Value> {

    private int blocks = 50;
    private int nWay = 5;
    private long expiration = 30 * 60 * 1000;
    private int entriesToDelete = 2;
    private int maxEntryPerBlock = 10;
    private CacheEviction<Key, Value> eviction = new LRUAlgorithm<>();

    /**
     * Number of memory blocks (or buckets) managed by the cache.<br/>
     * Default is 50
     *
     * @param blocks number of memory blocks (or buckets) that the cache manage
     * @return self
     * @throws java.lang.IllegalArgumentException if blocks is < 1
     */
    public NWayCacheBuilder<Key, Value> blocks(int blocks) {
        Preconditions.checkArgument(blocks > 0, "blocks has to be at least 1");
        this.blocks = blocks;
        return this;
    }

    /**
     * Size of every block (N-Way).<br/>
     * Default is 5
     *
     * @param nWay size of the blocks in number of entries
     * @return self
     * @throws java.lang.IllegalArgumentException if nWay is < 1
     */
    public NWayCacheBuilder<Key, Value> nWay(int nWay) {
        Preconditions.checkArgument(nWay > 0, "nWay has to be at least 1");
        this.nWay = nWay;
        return this;
    }

    /**
     * Number of entry stored in a block before a {@link java.lang.OutOfMemoryError} is raised.<br/>
     * Default is 10 (nWay *2)
     * @param maxEntryPerBlock number of entry per block before an exception
     * @return self
     * @throws java.lang.IllegalArgumentException if maxEntryPerBlock is < 2
     */
    public NWayCacheBuilder<Key, Value> maxEntryPerBlock(int maxEntryPerBlock) {
        Preconditions.checkArgument(maxEntryPerBlock > 1, "maxEntryPerBlock has to be at least 2");
        this.maxEntryPerBlock = maxEntryPerBlock;
        return this;
    }

    /**
     * Define the expiration time period used for calculate the expiration time.<br/>
     * It is used only by {@link LRUExpiredAlgorithm}. <br/>
     * Default is 30 minutes
     *
     * @param expiration time in milliseconds
     * @return self
     * @throws java.lang.IllegalArgumentException if expiration is < 1
     */
    public NWayCacheBuilder<Key, Value> expirationTime(long expiration) {
        Preconditions.checkArgument(expiration > 0, "Expiration time has to be > 0");
        this.expiration = expiration;
        return this;
    }

    /**
     * Define how many  entries will be deleted for each eviction.<br/>
     * It is used only by {@link LRUAlgorithm} and {@link MRUAlgorithm}<br/>
     * Default is 2
     *
     * @param entriesToDelete entries to delete for each eviction
     * @return self
     * @throws java.lang.IllegalArgumentException if entriesToDelete is < 1
     */
    public NWayCacheBuilder<Key, Value> entriesToDelete(int entriesToDelete) {
        Preconditions.checkArgument(entriesToDelete > 0, "At least 1 entry has to be deleted");
        this.entriesToDelete = entriesToDelete;
        return this;
    }

    /**
     * Define a custom algorithm for eviction. <br/>
     * Look the documentations for {@link CacheEviction}
     *
     * @param eviction your custom implementation
     * @return self
     */
    public NWayCacheBuilder<Key, Value> customEviction(CacheEviction<Key, Value> eviction) {
        this.eviction = eviction;
        return this;
    }

    /**
     * Default eviction algorithm that follow the LRU for the creation time.<br/>
     * See {@link LRUAlgorithm}
     *
     * @return self
     */
    public NWayCacheBuilder<Key, Value> LRUEviction() {
        this.eviction = new LRUAlgorithm<>();
        return this;
    }

    /**
     * Eviction LRU algorithm based on the access time.<br/>
     * See {@link LRUExpiredAlgorithm}
     *
     * @return self
     */
    public NWayCacheBuilder<Key, Value> LRUExpiredEviction() {
        this.eviction = new LRUExpiredAlgorithm<>();
        return this;
    }

    /**
     * Eviction algorithm that follow the MRU for the creation time.<br/>
     * See {@link MRUAlgorithm}
     *
     * @return self
     */
    public NWayCacheBuilder<Key, Value> MRUEviction() {
        this.eviction = new MRUAlgorithm<>();
        return this;
    }

    /**
     * Build the n-way cache
     *
     * @param loader mandatory and not null
     * @return the cache with the parameters chosen
     * @throws java.lang.NullPointerException if loader is null
     * @throws java.lang.IllegalStateException if maxEntryPerBlock < nWay
     */
    public Cache<Key, Value> build(CacheLoader<Key, Value> loader) {
        Preconditions.checkNotNull(loader, "Must implement a loader");
        Preconditions.checkState(maxEntryPerBlock >= nWay, "maxEntryPerBlock has to be major or equals to nWay");
        if (eviction instanceof LRUExpiredAlgorithm) {
            ((LRUExpiredAlgorithm) eviction).setExpiration(expiration);
        } else if (eviction instanceof LRUAlgorithm) {
            ((LRUAlgorithm) eviction).setEntriesToDelete(entriesToDelete);
        } else if (eviction instanceof MRUAlgorithm) {
            ((MRUAlgorithm) eviction).setEntriesToDelete(entriesToDelete);
        }
        NWayCache<Key, Value> cache = new NWayCache<>(blocks, nWay, maxEntryPerBlock);
        cache.setCacheLoader(loader);
        cache.setEviction(eviction);
        return cache;
    }
}
