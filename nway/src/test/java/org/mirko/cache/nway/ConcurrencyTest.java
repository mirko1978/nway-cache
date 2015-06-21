package org.mirko.cache.nway;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * <p>Run 2.000.000 cache hits with the entries values defined between [{@link #MIN_KEY}, {@link #MAX_KEY}]
 * in a multi-thread environment for highlight eventual concurrency problem</p>
 * <br/><br/>Created by Mirko Bernardoni on 30/05/15.
 *
 * @author Mirko Bernardoni
 * @version 1.0
 * @since 1.0
 */
public class ConcurrencyTest extends AbstractMultiThreads {
    public static final int MULTIPLIER = 30;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void shutdown() {
        super.shutdown();
    }

    /**
     * Run the implementation with {@link #CONSUMER} threads and random data
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testHitsRandom() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER);
        List<Future<Integer>> todo = new ArrayList<>(CONSUMER);

        // Sequential load in order to not use one core for loading
        loadRandomValues();
        consumeQueue(todo, executorService, CONSUMER, "Multiple Thread Random");
    }

    /**
     * Run the implementation with {@link #CONSUMER} threads and sequential data (0...N, 0...N, ...)
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testHitsSequential() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER);
        List<Future<Integer>> todo = new ArrayList<>(CONSUMER);

        // Sequential load in order to not use one core for loading
        loadRangeValues();

        consumeQueue(todo, executorService, CONSUMER, "Multiple Thread Sequential");
    }

    /**
     * Run the implementation with {@link #CONSUMER} * {@link #MULTIPLIER} threads and sequential data (0...N, 0...N, ...)
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testHitsWithALotOfThreads() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER * MULTIPLIER);
        List<Future<Integer>> todo = new ArrayList<>(CONSUMER * MULTIPLIER);

        // Sequential load in order to not use one core for loading
        loadRangeValues();

        consumeQueue(todo, executorService, CONSUMER * MULTIPLIER, "High Number of threads Sequential");
    }

}
