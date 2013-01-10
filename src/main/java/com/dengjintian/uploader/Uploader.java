package com.dengjintian.uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

/**
 * Upload a list of files to dropbox server.
 */
public class Uploader implements Callable<Boolean> {

    private static final Log log = LogFactory.getLog(Uploader.class);
    private String[]         fileList;

    public Uploader(String[] fileList){
        this.fileList = fileList;
    }

    public Boolean run() {
        if (fileList == null || fileList.length == 0) {
            log.error("fileList is empty!");
            return false;
        }

        // create dropbox api
        DropboxAPI<WebAuthSession> api = getDropboxApi();

        // upload each file
        long beginning = System.currentTimeMillis();
        for (String file : fileList) {
            uploadFile(file, api);
        }
        beginning = System.currentTimeMillis() - beginning;
        log.info("Upload finished in " + beginning + "ms !");
        return true;
    }

    private DropboxAPI<WebAuthSession> getDropboxApi() {
        AccessTokenPair accessTokenPair = new AccessTokenPair(Main.ACCESS_KEY, Main.ACCESS_SECRET);
        WebAuthSession session = new WebAuthSession(new AppKeyPair(Main.APP_KEY, Main.APP_SECRET), AccessType.DROPBOX,
                                                    accessTokenPair);
        DropboxAPI<WebAuthSession> api = new DropboxAPI<WebAuthSession>(session);
        return api;
    }

    private void uploadFile(String file, DropboxAPI<WebAuthSession> api) {
        File f = new File(file);
        if (!f.exists()) {
            log.error("File(" + file + ") does not exist!");
            return;
        }

        log.info("Uploading file " + file + " with size " + f.length() / 1024 + "KB");
        String dropboxFileName = file.startsWith("/") ? file : "/" + file;

        // try to get the old version of the file if exist
        Entry meta = null;
        try {
            meta = api.metadata(dropboxFileName, 1, null, false, null);
            if (meta != null) log.info("Old rev " + meta.rev);
        } catch (DropboxException e) {
            log.warn("File(" + file + ") may not exist in dropbox server!", e);
        }

        // do uploading
        try {
            meta = api.putFile(dropboxFileName, new FileInputStream(file), f.length(), meta == null ? null : meta.rev,
                               getProgressListener());
        } catch (Exception e) {
            log.error("Upload file(" + file + ") fail!", e);
            return;
        }
        log.info("Upload file(" + file + ") success! New rev is " + meta.rev);
    }

    /**
     * print the uploading progress.
     * 
     * @return
     */
    public ProgressListener getProgressListener() {
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytes, long total) {
                String anim = "|/-\\";
                int x = (int) (bytes * 1.0 / total * 100);
                String data = "\r" + anim.charAt(x % anim.length()) + " " + x + "%\r";
                try {
                    System.out.write(data.getBytes());
                } catch (IOException e) {
                    // do nothing, be quite if something wrong happens
                }
            }
        };
        return progressListener;
    }

    @Override
    public Boolean call() throws Exception {
        return this.run();
    }
}
