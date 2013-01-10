package com.dengjintian.uploader;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This program is a simple application to upload file to dropbox. <br/>
 * It can be used with cron to backup files.
 * <p>
 * It accept one system property "timeout" which means how long the process <br />
 * will wait for the finish of uploading. Default value is 1 minute.
 * </p>
 * <p>
 * Sample usage: java -Dtimeout=5 -jar dropbox.jar  xxx.text
 * </p>
 */
public class Main {

    private static final Log log           = LogFactory.getLog(Main.class);
    public static String     APP_KEY       = "";
    public static String     APP_SECRET    = "";
    public static String     ACCESS_KEY    = "";
    public static String     ACCESS_SECRET = "";

    public static void main(String[] args) throws IOException {
        // return if no files is specified
        if (args == null || args.length == 0) {
            log.error("File list is empty!");
            return;
        }

        // start uploading
        log.info("Start uploading file...");
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Uploader(args));
        new Thread(futureTask).start();

        // get the timeout and wait for finnish
        String timeoutString = System.getProperty("timeout");
        Long minutes = 1l;
        if (StringUtils.isNotBlank(timeoutString) && StringUtils.isNumeric(timeoutString)) {
            minutes = Long.valueOf(timeoutString);
        }
        log.warn("Max wait " + minutes + " minutes!");
        if (!futureTask.isDone()) {
            try {
                futureTask.get(minutes, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("Uploading is interrupted!", e);
            } catch (ExecutionException e) {
                log.error("Something wrong during uploading!", e);
            } catch (TimeoutException e) {
                log.error("Uploading timeout!", e);
            }
        }
        log.info("Finishing uploading file...");
    }
}
