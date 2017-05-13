package com.fil.shauni.command;

import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.util.DateFormat;
import com.fil.shauni.util.GeneralUtil;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author Shaunyl
 */
@Log4j
public abstract class ConfigCommandControl extends Command {

    @Override
    public Long call() throws Exception { return 0L; };
    
    @Override
    public long execute() {
        String currentDate = GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString());
        log.info("Task started at " + currentDate + "\n");

        long endTime = 0;
        String state = "completed";
        try {
            // add the parallelism here.. (parameter: cluster=<N>)
            validate();

            setup();

            long startTime = System.currentTimeMillis();
            run();
            endTime = System.currentTimeMillis() - startTime;

            takedown();
        } catch (ShauniException sh) {
            log.error(sh.getMessage());
            state = "aborted";
        }

        currentDate = GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString());
        log.info("\nTask " + state + " at " + currentDate + " with " + errorCount + " warning(s)\nElapsed time: " + endTime / 1e3 + " s");
        return endTime;
    }
}
