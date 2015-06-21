package org.mirko.cache.nway;

import org.mirko.cache.nway.algorithm.LRUAlgorithm;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>Console application for testing the cache performance.</p>
 * <p>This performance test is not indicative as performance of the systems because the CacheLoader is just generating a string.</p>
 * <p>The goal of the class is to highlight eventually threads scheduling delays compared to a simple and fast mono
 * thread cahce implementation. See {@link NWayCacheSingleThread}</p>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class PerformanceComparisonApplication extends AbstractMultiThreads {

    public static void main(String[] args) throws Exception {
        PerformanceComparisonApplication performanceComparison = new PerformanceComparisonApplication();

        System.out.println("**** Fastest implementation run two time because the first time the class loader is slowing the execution ****");
        performanceComparison.sequentialOneThreadFast();
        performanceComparison.sequentialOneThreadFast();

        performanceComparison.sequentialOneThread();

        performanceComparison.hitsSequential();

        performanceComparison.randomOneThread();

        performanceComparison.hitsRandom();
    }

    /**
     * Run the implementation with 1 thread and sequential data (0...N, 0...N, ...)
     *
     * @throws Exception
     */
    public void sequentialOneThread() throws Exception {
        setUp();
        // Sequential load in order to not use one core for loading
        loadRandomValues();
        AbstractMultiThreads.QueueConsumer consumer = new AbstractMultiThreads.QueueConsumer();
        long start = System.currentTimeMillis();
        int total = consumer.call();
        long time = System.currentTimeMillis() - start;
        System.out.println("One Thread Sequential Elaborated " + total + " in " + time + " ms");
        Assert.assertEquals(HITS, total);
        shutdown();
    }

    /**
     * Run the implementation with 1 thread and random data
     *
     * @throws Exception
     */
    public void randomOneThread() throws Exception {
        setUp();
        // Sequential load in order to not use one core for loading
        loadRandomValues();
        QueueConsumer consumer = new QueueConsumer();
        long start = System.currentTimeMillis();
        int total = consumer.call();
        long time = System.currentTimeMillis() - start;
        System.out.println("One thread Random Elaborated " + total + " in " + time + " ms");
        Assert.assertEquals(HITS, total);
        shutdown();
    }

    /**
     * Run the fast implementation with 1 thread and sequential data (0...N, 0...N, ...)
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void sequentialOneThreadFast() throws Exception {
        setUp();
        cache = new NWayCacheSingleThread<>(50, 5); // Same parameters than the builder
        ((NWayCacheSingleThread) cache).setCacheLoader(loader);
        LRUAlgorithm<Integer, String> lru = new LRUAlgorithm<>();
        lru.setEntriesToDelete(2); // same than the builder
        ((NWayCacheSingleThread) cache).setEviction(lru);

        loadRangeValues();
        QueueConsumer consumer = new QueueConsumer();
        long start = System.currentTimeMillis();
        int total = consumer.call();
        long time = System.currentTimeMillis() - start;
        System.out.println("Reference speed with fastest implementation and sequential values: Elaborated " + total + " in " + time + " ms");
        Assert.assertEquals(HITS, total);
        shutdown();
    }

    /**
     * Run the implementation with {@link #CONSUMER} threads and random data
     *
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     */
    public void hitsRandom() throws Exception {
        setUp();
        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER);
        List<Future<Integer>> todo = new ArrayList<>(CONSUMER);

        // Sequential load in order to not use one core for loading
        loadRandomValues();
        consumeQueue(todo, executorService, CONSUMER, "Multiple Thread Random");
        shutdown();
    }

    /**
     * Run the implementation with {@link #CONSUMER} threads and sequential data (0...N, 0...N, ...)
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void hitsSequential() throws Exception {
        setUp();
        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER);
        List<Future<Integer>> todo = new ArrayList<>(CONSUMER);

        // Sequential load in order to not use one core for loading
        loadRangeValues();

        consumeQueue(todo, executorService, CONSUMER, "Multiple Thread Sequential");
        shutdown();
    }
}
