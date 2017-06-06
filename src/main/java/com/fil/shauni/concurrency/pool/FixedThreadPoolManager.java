package com.fil.shauni.concurrency.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class FixedThreadPoolManager {

    private static ExecutorService executor;

    private FixedThreadPoolManager() {
    }

    public static ExecutorService getInstance(int nThreads, ThreadFactory factory) {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(nThreads, factory);
        }
        return executor;
    }

    public static void shutdownPool() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            executor = null;
        }
    }
}
