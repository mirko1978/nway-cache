package org.mirko.cache.nway.algorithm;

import org.mirko.cache.nway.CacheEntryStatus;
import org.junit.Assert;
import org.junit.Before;
/**
 * Test class for {@link MRUAlgorithmTest}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class MRUAlgorithmTest extends AlgorithmTest {

    private MRUAlgorithm<Integer, String> mru;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        super.setup();
        mru = new MRUAlgorithm<>();
    }

    @Override
    public void testEvictionNoEntryNoToDelete() throws Exception {
        mru.eviction(blocks);
    }

    @Override
    public void testEvictionNoToDelete() throws Exception {
        create10entries();
        mru.eviction(blocks);
    }

    @Override
    public void testEvictionNoEntry() throws Exception {
        mru.setEntriesToDelete(1);
        mru.eviction(blocks);
        Assert.assertEquals(0, blocks.size());
    }

    @Override
    public void testEvictionDeleteOne() throws Exception {
        mru.setEntriesToDelete(1);
        create10entries();
        mru.eviction(blocks);

        Assert.assertEquals(10, blocks.size());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(9).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(8).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(0).getStatus());
    }

    @Override
    public void testEvictionDeleteThree() throws Exception {
        mru.setEntriesToDelete(3);
        create10entries();
        mru.eviction(blocks);

        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(0).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(6).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(7).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(8).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(9).getStatus());
    }

    @Override
    public void testEvictionDeleteMore() throws Exception {
        mru.setEntriesToDelete(100);
        create10entries();
        mru.eviction(blocks);

        Assert.assertEquals(10, blocks.size());
        blocks.forEach(e -> Assert.assertEquals(CacheEntryStatus.DELETED, e.getStatus()));
    }

    @Override
    public void testEvictionDeleteAll() throws Exception {
        mru.setEntriesToDelete(10);
        create10entries();
        mru.eviction(blocks);

        Assert.assertEquals(10, blocks.size());
        blocks.forEach(e -> Assert.assertEquals(CacheEntryStatus.DELETED, e.getStatus()));
    }

}