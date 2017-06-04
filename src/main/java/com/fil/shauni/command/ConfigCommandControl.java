//package com.fil.shauni.command;
//
//import com.fil.shauni.exception.ShauniException;
//import com.fil.shauni.util.Sysdate;
//import lombok.extern.log4j.Log4j;
//
///**
// *
// * @author Shaunyl
// */
//@Log4j
//public abstract class ConfigCommandControl extends Command {
//
//    @Override
//    public Long call() throws Exception { return 0L; };
//    
//    @Override
//    public long execute() {
//        String currentDate = Sysdate.now(Sysdate.TIMEONLY);
//        log.info("Task started at " + currentDate + "\n");
//
//        long endTime = 0;
//        String state = "completed";
//        try {
//            // add the parallelism here.. (parameter: cluster=<N>)
//            validate();
//
//            setup();
//
//            long startTime = System.currentTimeMillis();
//            runThread();
//            endTime = System.currentTimeMillis() - startTime;
//
//            takedownThread();
//        } catch (ShauniException sh) {
//            log.error(sh.getMessage());
//            state = "aborted";
//        }
//
//        currentDate = Sysdate.now(Sysdate.TIMEONLY);
//        log.info("\nTask " + state + " at " + currentDate + " with " + getErrorCount() + " warning(s)\nElapsed time: " + endTime / 1e3 + " s");
//        return endTime;
//    }
//}
