package org.mirko.cache.nway;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test class for {@link AbstractCache}
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class AbstractCacheTest {
    private AbstractCacheWrapper cache;

    @Before
    public void setUp() throws Exception {
        cache = new AbstractCacheWrapper();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Removal listener tests
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    @Test
    public void testAddRemovalListener() throws Exception {
        RemovalListener<Integer, String> removalListener = EasyMock.createNiceMock(RemovalListener.class);
        EasyMock.replay(removalListener);
        cache.addRemovalListener(removalListener);
        Assert.assertFalse(cache.getRemovalListeners().isEmpty());

    }

    @Test
    public void testAddNulllRemovalListener() throws Exception {
        cache.addRemovalListener(null);
        Assert.assertTrue(cache.getRemovalListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveRemovalListener() throws Exception {
        RemovalListener<Integer, String> removalListener = EasyMock.createNiceMock(RemovalListener.class);
        EasyMock.replay(removalListener);
        cache.getRemovalListeners().add(removalListener);

        cache.removeRemovalListener(removalListener);
        Assert.assertTrue(cache.getRemovalListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveFromEmptyRemovalListener() throws Exception {
        RemovalListener<Integer, String> removalListener = EasyMock.createNiceMock(RemovalListener.class);
        EasyMock.replay(removalListener);
        cache.removeRemovalListener(removalListener);
        Assert.assertTrue(cache.getRemovalListeners().isEmpty());
    }

    @Test
    public void testRemoveNullFromEmtpyRemovalListener() throws Exception {
        cache.removeRemovalListener(null);
        Assert.assertTrue(cache.getRemovalListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveNullRemovalListener() throws Exception {
        RemovalListener<Integer, String> removalListener = EasyMock.createNiceMock(RemovalListener.class);
        EasyMock.replay(removalListener);
        cache.getRemovalListeners().add(removalListener);
        cache.removeRemovalListener(null);
        Assert.assertEquals(1, cache.getRemovalListeners().size());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testFireRemovalListenerEmpty() throws Exception {
        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireRemovalListener(entry, RemovalCause.USER);
    }

    @Test(expected = NullPointerException.class)
    public void testFireRemovalListenerNulls() throws Exception {
        cache.fireRemovalListener(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testFireRemovalListenerNullRemovalCause() throws Exception {
        cache.fireRemovalListener(null, RemovalCause.USER);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testFireRemovalListenerNullEntry() throws Exception {
        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireRemovalListener(entry, null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireRemovalOneListener() throws Exception {
        RemovalListenerWrapper removalListener = new RemovalListenerWrapper();
        cache.getRemovalListeners().add(removalListener);


        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireRemovalListener(entry, RemovalCause.USER);
        Assert.assertEquals(1, removalListener.called);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireRemovalListenerMultipleTimes() throws Exception {
        RemovalListenerWrapper removalListener = new RemovalListenerWrapper();
        cache.getRemovalListeners().add(removalListener);
        cache.getRemovalListeners().add(removalListener);
        cache.getRemovalListeners().add(removalListener);


        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireRemovalListener(entry, RemovalCause.USER);
        Assert.assertEquals(3, removalListener.called);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireRemovalTwoListener() throws Exception {
        RemovalListenerWrapper removalListener1 = new RemovalListenerWrapper();
        cache.getRemovalListeners().add(removalListener1);
        RemovalListenerWrapper removalListener2 = new RemovalListenerWrapper();
        cache.getRemovalListeners().add(removalListener2);


        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireRemovalListener(entry, RemovalCause.USER);
        Assert.assertEquals(1, removalListener1.called);
        Assert.assertEquals(1, removalListener2.called);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cached listener tests
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Test
    public void testAddCachedListener() throws Exception {
        CachedListener<Integer, String> cachedListener = EasyMock.createNiceMock(CachedListener.class);
        EasyMock.replay(cachedListener);
        cache.addCachedListener(cachedListener);
        Assert.assertFalse(cache.getCachedListeners().isEmpty());

    }

    @Test
    public void testAddNulllCachedlListener() throws Exception {
        cache.addCachedListener(null);
        Assert.assertTrue(cache.getCachedListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveCachedListener() throws Exception {
        CachedListener<Integer, String> cachedListener = EasyMock.createNiceMock(CachedListener.class);
        EasyMock.replay(cachedListener);
        cache.getCachedListeners().add(cachedListener);

        cache.removeCachedListener(cachedListener);
        Assert.assertTrue(cache.getCachedListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveFromEmptyCachedListener() throws Exception {
        CachedListener<Integer, String> cacheListener = EasyMock.createNiceMock(CachedListener.class);
        EasyMock.replay(cacheListener);
        cache.removeCachedListener(cacheListener);
        Assert.assertTrue(cache.getCachedListeners().isEmpty());
    }

    @Test
    public void testRemoveNullFromEmtpyCachedListener() throws Exception {
        cache.removeCachedListener(null);
        Assert.assertTrue(cache.getCachedListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveNullCachedListener() throws Exception {
        CachedListener<Integer, String> cachedListener = EasyMock.createNiceMock(CachedListener.class);
        EasyMock.replay(cachedListener);
        cache.getCachedListeners().add(cachedListener);
        cache.removeCachedListener(null);
        Assert.assertEquals(1, cache.getCachedListeners().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireCachedListenerEmpty() throws Exception {
        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireCachedListener(entry);
    }

    @Test(expected = NullPointerException.class)
    public void testFireCachedListenerNulls() throws Exception {
        cache.fireCachedListener(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireCachedOneListener() throws Exception {
        CachedListenerWrapper cachedListener = new CachedListenerWrapper();
        cache.getCachedListeners().add(cachedListener);

        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireCachedListener(entry);
        Assert.assertEquals(1, cachedListener.called);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireCachedListenerMultipleTimes() throws Exception {
        CachedListenerWrapper cachedListener = new CachedListenerWrapper();
        cache.getCachedListeners().add(cachedListener);
        cache.getCachedListeners().add(cachedListener);
        cache.getCachedListeners().add(cachedListener);


        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireCachedListener(entry);
        Assert.assertEquals(3, cachedListener.called);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireCachedTwoListener() throws Exception {
        CachedListenerWrapper cachedListener1 = new CachedListenerWrapper();
        cache.getCachedListeners().add(cachedListener1);
        CachedListenerWrapper cachedListener2 = new CachedListenerWrapper();
        cache.getCachedListeners().add(cachedListener2);


        CacheEntry<Integer, String> entry = EasyMock.createNiceMock(CacheEntry.class);
        EasyMock.replay(entry);
        cache.fireCachedListener(entry);
        Assert.assertEquals(1, cachedListener1.called);
        Assert.assertEquals(1, cachedListener1.called);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miss listener tests
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Test
    public void testAddMissListener() throws Exception {
        MissListener<Integer> missListener = EasyMock.createNiceMock(MissListener.class);
        EasyMock.replay(missListener);
        cache.addMissListener(missListener);
        Assert.assertFalse(cache.getMissListeners().isEmpty());

    }

    @Test
    public void testAddNulllMisslListener() throws Exception {
        cache.addMissListener(null);
        Assert.assertTrue(cache.getMissListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveMissListener() throws Exception {
        MissListener<Integer> missListener = EasyMock.createNiceMock(MissListener.class);
        EasyMock.replay(missListener);
        cache.getMissListeners().add(missListener);

        cache.removeMissListener(missListener);
        Assert.assertTrue(cache.getMissListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveFromEmptyMissListener() throws Exception {
        MissListener<Integer> missListener = EasyMock.createNiceMock(MissListener.class);
        EasyMock.replay(missListener);
        cache.removeMissListener(missListener);
        Assert.assertTrue(cache.getMissListeners().isEmpty());
    }

    @Test
    public void testRemoveNullFromEmtpyMissListener() throws Exception {
        cache.removeMissListener(null);
        Assert.assertTrue(cache.getMissListeners().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveNullMissListener() throws Exception {
        MissListener<Integer> missListener = EasyMock.createNiceMock(MissListener.class);
        EasyMock.replay(missListener);
        cache.getMissListeners().add(missListener);
        cache.removeMissListener(null);
        Assert.assertEquals(1, cache.getMissListeners().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireMissListenerEmpty() throws Exception {
        cache.fireMissListener(10);
    }

    @Test(expected = NullPointerException.class)
    public void testFireMissListenerNulls() throws Exception {
        cache.fireMissListener(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireMissOneListener() throws Exception {
        MissListenerWrapper missListener = new MissListenerWrapper();
        cache.getMissListeners().add(missListener);

        cache.fireMissListener(10);
        Assert.assertEquals(1, missListener.called);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireMissListenerMultipleTimes() throws Exception {
        MissListenerWrapper missListener = new MissListenerWrapper();
        cache.getMissListeners().add(missListener);
        cache.getMissListeners().add(missListener);
        cache.getMissListeners().add(missListener);

        cache.fireMissListener(10);
        Assert.assertEquals(3, missListener.called);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFireMissTwoListener() throws Exception {
        MissListenerWrapper missListener1 = new MissListenerWrapper();
        cache.getMissListeners().add(missListener1);
        MissListenerWrapper missListener2 = new MissListenerWrapper();
        cache.getMissListeners().add(missListener2);

        cache.fireMissListener(10);
        Assert.assertEquals(1, missListener1.called);
        Assert.assertEquals(1, missListener2.called);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Other class tests
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Test
    public void testSetEviction() {
        CacheEviction<Integer, String> eviction = EasyMock.createNiceMock(CacheEviction.class);
        EasyMock.replay(eviction);
        cache.setEviction(eviction);
        Assert.assertEquals(eviction, cache.getEviction());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetCacheLoader() {
        CacheLoader<Integer, String> cacheLoader = EasyMock.createNiceMock(CacheLoader.class);
        EasyMock.replay(cacheLoader);
        cache.setCacheLoader(cacheLoader);
        Assert.assertEquals(cacheLoader, cache.getCacheLoader());
    }

    private class RemovalListenerWrapper implements RemovalListener<Integer, String> {
        public int called;

        @Override
        public void onRemoval(RemovalNotification<Integer, String> notification) {
            called++;
        }
    }

    private class CachedListenerWrapper implements CachedListener<Integer, String> {
        public int called;

        @Override
        public void onCache(CacheNotification<Integer, String> notification) {
            called++;
        }
    }

    private class MissListenerWrapper implements MissListener<Integer> {
        public int called;

        @Override
        public void onMiss(Integer key) {
            called++;
        }
    }

    private class AbstractCacheWrapper extends AbstractCache<Integer, String> {

        public List<RemovalListener<Integer, String>> getRemovalListeners() {
            return this.removalListeners;
        }

        public List<CachedListener<Integer, String>> getCachedListeners() {
            return this.cachedListeners;
        }

        public List<MissListener<Integer>> getMissListeners() {
            return this.missListeners;
        }

        @Override
        public void put(Integer integer, String s) {
        }

        @Override
        public String get(Integer integer) throws CacheLoaderException {
            return null;
        }

        @Override
        public void remove(Integer integer) {
        }
    }
}