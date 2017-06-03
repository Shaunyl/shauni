package com.fil.shauni.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Shaunyl
 */
@NoArgsConstructor @Log4j2
public abstract class Command<T> implements Callable<Long> {
    @Getter
    private int errorCount = 0;

    @Parameter(names = "--help", help = true, description = "Prints the help") @Getter
    protected boolean help = false;

    @Getter @Parameter(required = true, arity = 1)
    protected final List<String> cmd = Lists.newArrayList(1);

    @Getter
    protected String name;

    @Getter
    private String description;

    protected int workers, threads;

    protected String _thread;

    protected final List<FutureTask<Void>> futures = new ArrayList<>();

    protected ExecutorService executorService;
    
    public void incrementErrorCount() {
        this.errorCount++;
    }

    /**
     * Add a description to the command
     *
     * @param description the command's description
     * @return the command itself
     */
    public Command withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Setup the command
     *
     * @throws com.fil.shauni.exception.ShauniException
     */
    public void setup() throws ShauniException {
        this.executorService = ThreadPoolManager.getInstance();
        setDegree();
//        produceSubworkSets();
    }

    /**
     * Set the internal parallelism of the command, if there's any
     * By default is 1 (serial)
     * If you set a degree higher than one, you need to define a "split of the work" strategy implementing a WorkSplitter
     */
    protected void setDegree() {
        this.workers = 1;
    }
    
//    protected <T> Map<Integer, T[]> produceSubworkSets(Class<T> clazz, WorkSplitter<T> workSplitter) {
//        return workSplitter.splitWork(clazz, workers, );
//    }

    /**
     * Takedown the command
     */
    public void takedownThread() {
    }

    /**
     * Validate the syntax and the options of the command
     *
     * @return True if command is corrextly validated. Otherwise False
     * @throws com.fil.shauni.exception.ShauniException
     */
    public boolean validate() throws ShauniException {
        return true;
    }

    public void runThread() throws ShauniException {
        for (int i = 0; i < workers; i++) {
            this.runWorker(i);
        }

        for (int i = 0; i < workers; i++) {
            this.getWorkerResult(i);
        }
    }
    
    /*
    // SERIAL
    public void runThread() {
        runWorker();
    }
    
    public void runWorker() {
        for (int i = 0; i < objects.size(); i++) {
            runTask(i, objects);
        }
        postWorker();
    }
    
    */

    public void runWorker(final int i) {
        List<T> set = this.extractWorkSet(i);
        FutureTask<Void> future = new FutureTask<>(() -> {
            log.debug("worker {} is preparing for the export", i);
            for (int t = 0; t < Math.max(1, set.size()); t++) {
                runTask(i, t, set);
            }
            log.debug("worker {} has terminated", i);
        }, null);
        this.futures.add(future);
        this.executorService.execute(future);
        this.postWorker(i);
    }

    /**
     * A worker is passed and its workset is returned.
     *
     * @param w the worker id
     * @return the workset assigned to the worker
     */
    protected List<T> extractWorkSet(final int w) {
        return new ArrayList<>();
    }

    /**
     * Do something after the worker completes its task
     *
     * @param w the worker id
     */
    protected void postWorker(final int w) {
    }

    protected abstract void runTask(final int w, final int t, final List<T> set);

    protected void getWorkerResult(final int w) throws ShauniException {
        FutureTask<Void> future = futures.get(w);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new ShauniException(1004, String.format("Worker %d interrupted abnormally..\n --> %s", w, e.getMessage()));
        }
    }

    protected abstract void initThread(final int i);

    /**
     * Execute the command
     *
     * @return elapsed time (s)
     */
    public long execute() {
        long start = System.currentTimeMillis();
        try {
            if (!validate()) { // FIXME.. cannot see message of failing.
                throw new ShauniException(1100, "Validation failed.");
            }
            // Setup the command
            this.setup();

            for (int i = 0; i < threads; i++) {
                this.initThread(i);

                long startTime = System.currentTimeMillis();
                this.runThread();

                long finishTime = System.currentTimeMillis() - startTime;
                
                log.info("Session {} task #{} finished in {} ms", _thread, i, finishTime / 1e3);

                this.takedownThread();
            }
        } catch (ShauniException e) {
            errorCount++;
            log.error(e.getMessage());
        }
        return System.currentTimeMillis() - start;
    }
}