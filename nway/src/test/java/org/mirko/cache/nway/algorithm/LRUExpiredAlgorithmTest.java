package org.mirko.cache.nway.algorithm;

import org.mirko.cache.nway.CacheEntry;
import org.mirko.cache.nway.CacheEntryStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link LRUExpiredAlgorithm}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class LRUExpiredAlgorithmTest extends AlgorithmTest {

    private static final long PAST = -60 * 1000l;
    private static final long FUTURE = 60 * 1000l;
    private LRUExpiredAlgorithm<Integer, String> lru;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        super.setup();
        lru = new LRUExpiredAlgorithm<>();
    }

    @Override
    public void testEvictionNoEntryNoToDelete() throws Exception {
        lru.eviction(blocks);
    }

    @Override
    public void testEvictionNoToDelete() throws Exception {
        create10entries(PAST);
        lru.eviction(blocks);
    }

    @Override
    public void testEvictionNoEntry() throws Exception {
        lru.setExpiration(1);
        lru.eviction(blocks);
        Assert.assertEquals(0, blocks.size());
    }

    @Override
    public void testEvictionDeleteOne() throws Exception {
        create10entries(FUTURE);
        ((MockEntry) (blocks.get(5))).setDelta(PAST); // The first was accessed 1 min in the past
        lru.setExpiration(100);
        lru.eviction(blocks);
        Assert.assertEquals(10, blocks.size());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(5).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(0).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(9).getStatus());
    }

    @Override
    public void testEvictionDeleteThree() throws Exception {
        create10entries(FUTURE);
        ((MockEntry) (blocks.get(0))).setDelta(PAST); // The first was accessed 10 sec in the past
        ((MockEntry) (blocks.get(5))).setDelta(PAST);
        ((MockEntry) (blocks.get(8))).setDelta(PAST);
        lru.setExpiration(100);
        lru.eviction(blocks);

        Assert.assertEquals(10, blocks.size());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(0).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(5).getStatus());
        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(8).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(1).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(9).getStatus());
    }

    @Override
    public void testEvictionDeleteMore() throws Exception {
    }

    @Override
    public void testEvictionDeleteAll() throws Exception {
        create10entries(PAST);
        lru.setExpiration(100);
        lru.eviction(blocks);

        Assert.assertEquals(10, blocks.size());
        blocks.forEach(e -> Assert.assertEquals(CacheEntryStatus.DELETED, e.getStatus()));
    }

    @Test
    public void testEvictionAtLeastOne() {
        create10entries(FUTURE);
        lru.setExpiration(100);
        lru.eviction(blocks);
        Assert.assertEquals(10, blocks.size());

        Assert.assertEquals(CacheEntryStatus.DELETED, blocks.get(0).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(1).getStatus());
        Assert.assertEquals(CacheEntryStatus.ACTIVE, blocks.get(9).getStatus());
    }

    protected void create10entries(long delta) {
        for (int i = 0; i < 10; i++) {
            MockEntry entry = new MockEntry();
            entry.setDelta(delta);
            entry.setKey(i);
            blocks.add(entry);
        }
    }

    private class MockEntry implements CacheEntry<Integer, String> {
        private long delta;
        private Integer key;
        private CacheEntryStatus status = CacheEntryStatus.ACTIVE;

        public void setDelta(long delta) {
            this.delta = delta;
        }

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        @Override
        public String getValue() {
            return null;
        }

        @Override
        public long getAccessTime() {
            return System.currentTimeMillis() + delta;
        }

        @Override
        public CacheEntryStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(CacheEntryStatus status) {
            this.status = status;
        }
    }
}