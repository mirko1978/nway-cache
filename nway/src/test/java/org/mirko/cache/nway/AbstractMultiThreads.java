package org.mirko.cache.nway;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * <p>Abstract class used for creating the data structures (concurrent queue) and common functionality
 * (like key and values creation) for running multi threads tests</p>
 *
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class AbstractMultiThreads {

    public static final int MAX_KEY = 100;
    public static final int MIN_KEY = 10;
    public static final int CONSUMER = 4; // Number of core
    public static final int HITS = 2000000;

    public static final String VALUE = "V=";
    public MockLoader loader;
    public Cache<Integer, String> cache;
    public ConcurrentLinkedQueue<Integer> queue;

    public void setUp() throws Exception {
        loader = new MockLoader();
        cache = new NWayCacheBuilder<Integer, String>().build(loader);
        queue = new ConcurrentLinkedQueue<>();
    }

    public void shutdown() {
        System.out.flush();
        loader = null;
        cache = null;
        queue = null;
        System.gc();
    }

    /**
     * Get a random number in a interval
     *
     * @param min min value
     * @param max max value
     * @return random value
     */
    public int random(int min, int max) {
        Random rand = new Random();
        int randInt = Math.abs(rand.nextInt());
        int value = randInt % max;
        return value < min ? randInt % min : value;
    }

    /**
     * Add to the queries random numbers [{@link #MIN_KEY},{@link #MAX_KEY}]
     */
    public void loadRandomValues() {
        for (int i = 0; i < HITS; i++) {
            int key = random(MIN_KEY, MAX_KEY);
            queue.add(key);
        }
    }

    /**
     * Load the values sequentially from {@link #MIN_KEY} to {@link #MAX_KEY}
     */
    public void loadRangeValues() {
        int key = MIN_KEY;
        for (int i = 0; i < HITS; i++) {
            queue.add(key);
            key++;
            if (key >= MAX_KEY) {
                key = MIN_KEY;
            }
        }
    }

    /**
     * Consume the queue and wait for the threads the termination
     * @param todo background actions
     * @param executorService executor
     * @param worker worker
     * @param test test description
     */
    public void consumeQueue(List<Future<Integer>> todo, ExecutorService executorService, int worker, String test) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < worker; i++) {
            todo.add(executorService.submit(new QueueConsumer()));
        }
        todo.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
        executorService.shutdown();
        long time = System.currentTimeMillis() - start;
        int total = todo.stream().mapToInt(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                Assert.fail(e.getMessage());
            }
            return 0;
        }).sum();
        System.out.println("Test " + test + " Elaborated " + total + " in " + time + " ms");
        Assert.assertEquals(HITS, total);
    }

    public class MockLoader implements CacheLoader<Integer, String> {
        List<Integer> loaded = new ArrayList<>();

        @Override
        public String load(Integer key) {
            // Only one call for the same key
            Assert.assertFalse(loaded.contains(key));
            return VALUE + key;
        }
    }

    public class QueueConsumer implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            int count = 0;
            while (true) {
                try {
                    Integer key = queue.poll();
                    if (key == null) {
                        break;
                    }
                    count++;
                    Assert.assertEquals(VALUE + key, cache.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
            }
            return count;
        }
    }
}
