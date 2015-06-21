package org.mirko.cache.nway.algorithm;

import org.mirko.cache.nway.CacheEntryStatus;
import org.junit.Assert;
import org.junit.Before;

/**
 * Test class for {@link LRUAlgorithm}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class LRUAlgorithmTest extends AlgorithmTest {

    private LRUAlgorithm<Integer, String> lru;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        super.setup();
        lru = new LRUAlgorithm<>();
    }

    @Override
    public void testEvictionNoEntryNoToDelete() throws Exception {
        lru.eviction(blocks);
    }

    @Override
    public void testEvictionNoToDelete() throws Exception {
        create10entries();
        lru.eviction(blocks);
    }

    @Override
    public void testEvictionNoEntry() throws Exception {
        lru.setEntriesToDelete(1);
        lru.eviction(blocks);
        Assert.assertEquals(0, blocks.size());
    }

    @Override
    public void testEvictionDeleteOne() throws Exception {
        lru.setEntriesToDelete(1);
        create10entries();
        lru.eviction(blocks);
        Assert.assertEquals(10, blocks.size());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(0).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(1).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(9).getStatus());
    }

    @Override
    public void testEvictionDeleteThree() throws Exception {
        lru.setEntriesToDelete(3);
        create10entries();
        lru.eviction(blocks);

        Assert.assertEquals(10, blocks.size());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(0).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(1).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(2).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(3).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(9).getStatus());
    }

    @Override
    public void testEvictionDeleteMore() throws Exception {
        lru.setEntriesToDelete(100);
        create10entries();
        lru.eviction(blocks);
        blocks.forEach(e -> Assert.assertEquals(CacheEntryStatus.DELETED, e.getStatus()));
    }

    @Override
    public void testEvictionDeleteAll() throws Exception {
        lru.setEntriesToDelete(10);
        create10entries();
        lru.eviction(blocks);

        blocks.forEach(e -> Assert.assertEquals(CacheEntryStatus.DELETED, e.getStatus()));
    }

}