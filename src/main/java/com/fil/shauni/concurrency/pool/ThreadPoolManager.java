package com.fil.shauni.concurrency.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Shaunyl
 */
public class ThreadPoolManager {

    private static ExecutorService executor;

    private ThreadPoolManager() {
    }

    public static ExecutorService getInstance() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
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
