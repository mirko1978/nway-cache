package org.mirko.cache.nway.algorithm;

import org.mirko.cache.nway.CacheEntry;
import org.mirko.cache.nway.CacheEntryStatus;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for test the eviction algorithms
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public abstract class AlgorithmTest {
    protected List<CacheEntry<Integer, String>> blocks;
    protected List<CacheEntry<Integer, String>> blocksBefore;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        blocks = new ArrayList<>();
        blocksBefore = new ArrayList<>();
        EasyMock.replay();
    }

    @Test(expected = IllegalArgumentException.class)
    public abstract void testEvictionNoEntryNoToDelete() throws Exception;

    @Test(expected = IllegalArgumentException.class)
    public abstract void testEvictionNoToDelete() throws Exception;

    @Test
    public abstract void testEvictionNoEntry() throws Exception;

    @Test
    public abstract void testEvictionDeleteOne() throws Exception;

    @Test
    public abstract void testEvictionDeleteThree() throws Exception;

    @Test
    public abstract void testEvictionDeleteMore() throws Exception;

    @Test
    public abstract void testEvictionDeleteAll() throws Exception;

    @SuppressWarnings("unchecked")
    protected void create10entries() {
        for (int i = 0; i < 10; i++) {
            CacheEntry<Integer, String> entry = new Entry();
            blocks.add(entry);
            blocksBefore.add(entry);
        }
    }

    protected class Entry implements CacheEntry<Integer, String> {
        private long creationTime;
        private Integer key;
        private String value;
        private long accessTime;
        private CacheEntryStatus status = CacheEntryStatus.ACTIVE;

        public long getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }

        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public long getAccessTime() {
            return accessTime;
        }

        public void setAccessTime(long accessTime) {
            this.accessTime = accessTime;
        }

        public CacheEntryStatus getStatus() {
            return status;
        }

        public void setStatus(CacheEntryStatus status) {
            this.status = status;
        }
    }
}
