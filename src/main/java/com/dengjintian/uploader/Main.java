package com.dengjintian.uploader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * This program is a simple application to upload file to dropbox. <br/>
 * It can be used with cron to backup files.
 * <p>
 * It accept one system property "timeout" which means how long the process <br />
 * will wait for the finish of uploading.    Default value is 1 minute.
 * </p>
 * <p>
 * Sample usage: java -Dtimeout=5 -jar dropbox.jar
 * </p>
 */
public class Main {

    static final String APP_KEY = "";
    static final String APP_SECRET = "";
    static final String ACCESS_KEY = "";
    static final String ACCESS_SECRET = "";
    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            log.info("File list is empty!");
            return;
        }
        log.info("Start uploading file...");

        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Uploader(args));
        new Thread(futureTask).start();

        //get the timeout
        String timeoutString = System.getProperty("timeout");
        Long minutes = 1l;
        if (StringUtils.isNotBlank(timeoutString) && StringUtils.isNumeric(timeoutString)) {
            minutes = Long.valueOf(timeoutString);
        }
        log.warn("Max wait time " + minutes + " minutes!");

        if (!futureTask.isDone()) {
            try {
                futureTask.get(minutes, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("Something wrong happen!", e);
            }
        }

        log.info("Finishing uploading file...");
    }
}
