package org.mirko.cache.nway;

import org.mirko.cache.nway.algorithm.LRUAlgorithm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for {@link NWayCache}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class NWayCacheTest {
    private static final int BUCKETS = 8;
    private static final int NWAY = 2;
    private static final String VALUE = "Value for ";

    private NWayCache<Integer, String> cache;
    private MockLoader loader;

    @Before
    public void setUp() throws Exception {
        cache = new NWayCache<>(BUCKETS, NWAY, NWAY * 2);
        loader = new MockLoader();
        CacheEviction<Integer, String> eviction = new LRUAlgorithm<>();
        ((LRUAlgorithm) eviction).setEntriesToDelete(1);
        cache.setEviction(eviction);
    }

    @Test
    public void testPutKVNullLoader() throws Exception {
        cache.put(5, "Test");
        Assert.assertTrue(cache.exist(5));
    }

    @Test
    public void testPutKVWithLoader() throws Exception {
        cache.put(5, "Test");
        cache.setCacheLoader(loader);
        Assert.assertTrue(cache.exist(5));
    }

    @Test(expected = IllegalStateException.class)
    public void testPutSmallerMaxEntries() throws Exception {
        cache = new NWayCache<>(BUCKETS, NWAY, NWAY - 1);
        Assert.fail();
    }

    @Test(expected = OutOfMemoryError.class)
    public void testPutTooManyEntries() throws Exception {
        cache = new NWayCache<>(1, 2, 4);
        cache.setEviction(block -> {
        }); // do not evict
        cache.put(0, "Test bucket 0, nway 0");
        cache.put(1, "Test bucket 0, nway 1");
        cache.put(2, "Test bucket 0, nway 2");
        cache.put(3, "Test bucket 0, nway 3");
        cache.put(4, "Test bucket 0, nway 4");
        Assert.fail();
    }

    @Test
    public void testPutKVFillBucket() throws Exception {
        cache.put(0, "Test bucket 0, nway 0");
        cache.put(8, "Test bucket 0, nway 1");

        Assert.assertTrue(cache.exist(0));
        Assert.assertTrue(cache.exist(8));
    }

    @Test
    public void testPutKVOverfillBucket() throws Exception {
        cache.put(0, "Test bucket 0, nway 0");
        cache.put(8, "Test bucket 0, nway 1");
        cache.put(16, "Collision bucket 0, nway 0");

        Assert.assertTrue(cache.exist(8));
        Assert.assertFalse(cache.exist(0));
        Assert.assertTrue(cache.exist(16));
    }

    @Test(expected = NullPointerException.class)
    public void testPutKVNullKeyValue() throws Exception {
        cache.put(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testPutKVNullKey() throws Exception {
        cache.put(null, "Test");
    }

    @Test(expected = NullPointerException.class)
    public void testPutKVNullValue() throws Exception {
        cache.put(5, null);
    }

    @Test
    public void testPutKVSubstitution() throws Exception {
        cache.setCacheLoader(loader);
        cache.put(0, "Test bucket 0, nway 0");
        Assert.assertTrue(cache.exist(0));
        Assert.assertEquals("Test bucket 0, nway 0", cache.get(0));

        cache.put(0, "Substitution");
        Assert.assertTrue(cache.exist(0));
        Assert.assertEquals("Substitution", cache.get(0));
    }


    @Test(expected = NullPointerException.class)
    public void testGetNullLoader() throws Exception {
        String value = cache.get(5);
        Assert.assertTrue(cache.exist(5));
        Assert.assertEquals(VALUE + 5, value);
    }

    @Test
    public void testGetNotExisting() throws Exception {
        cache.setCacheLoader(loader);
        String value = cache.get(5);
        Assert.assertTrue(cache.exist(5));
        Assert.assertEquals(VALUE + 5, value);
    }

    @Test
    public void testGetFillBucket() throws Exception {
        cache.setCacheLoader(loader);
        String first = cache.get(0);
        String second = cache.get(8);

        Assert.assertTrue(cache.exist(0));
        Assert.assertEquals(VALUE + 0, first);
        Assert.assertTrue(cache.exist(8));
        Assert.assertEquals(VALUE + 8, second);
    }

    @Test
    public void testGetOverfillBucket() throws Exception {
        cache.setCacheLoader(loader);
        String first = cache.get(0);
        Assert.assertEquals(VALUE + 0, first);
        String second = cache.get(8);
        String third = cache.get(16);

        Assert.assertTrue(cache.exist(8));
        Assert.assertEquals(VALUE + 8, second);
        Assert.assertFalse(cache.exist(0));
        Assert.assertTrue(cache.exist(16));
        Assert.assertEquals(VALUE + 16, third);
    }

    @Test(expected = NullPointerException.class)
    public void testGetNullKey() throws Exception {
        cache.get(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetNullValue() throws Exception {
        cache.setCacheLoader(value -> null);
        cache.get(0);
    }

    @Test
    public void testGetFromCache() throws Exception {
        cache.setCacheLoader(loader);
        String first = cache.get(0);
        Assert.assertEquals(VALUE + 0, first);
        String second = cache.get(0);

        Assert.assertTrue(cache.exist(0));
        Assert.assertEquals(VALUE + 0, first);
        Assert.assertEquals(VALUE + 0, second);
    }

    @Test
    public void testRemove() throws Exception {
        cache.setCacheLoader(loader);
        cache.get(0);
        String second = cache.get(8);

        cache.remove(0);
        Assert.assertFalse(cache.exist(0));
        Assert.assertTrue(cache.exist(8));
        Assert.assertEquals(VALUE + 8, second);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() throws Exception {
        cache.setCacheLoader(loader);
        cache.get(0);
        cache.remove(null);
    }

    @Test
    public void testRemoveFromEmpty() throws Exception {
        cache.setCacheLoader(loader);
        cache.remove(1);
    }

    @Test(expected = CacheLoaderException.class)
    public void testExceptionFromCacheLoader() throws Exception {
        cache.setCacheLoader(k -> {throw new Exception("Exception generated form testExceptionFromCacheLoader -- don't worry!");});
        cache.get(1);
    }

    private class MockLoader implements CacheLoader<Integer, String> {
        List<Integer> loaded = new ArrayList<>();

        @Override
        public String load(Integer key) {
            // Only one call for the same key
            Assert.assertFalse(loaded.contains(key));
            return VALUE + key;
        }
    }

}