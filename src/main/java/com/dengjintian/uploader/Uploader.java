package com.dengjintian.uploader;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

/**
 * Upload a list of files to dropbox server.
 */
public class Uploader implements Callable<Boolean> {

    private static final Log log = LogFactory.getLog(Uploader.class);
    private String[] fileList;

    public Uploader(String[] fileList) {
        this.fileList = fileList;
    }

    public Boolean run() {
        if (fileList == null || fileList.length == 0) {
            log.error("fileList is empty!");
            return false;
        }

        //create dropbox api
        AccessTokenPair accessTokenPair = new AccessTokenPair(Main.ACCESS_KEY, Main.ACCESS_SECRET);
        WebAuthSession session = new WebAuthSession(new AppKeyPair(Main.APP_KEY, Main.APP_SECRET), AccessType.DROPBOX,
                accessTokenPair);

        DropboxAPI<WebAuthSession> api = new DropboxAPI<WebAuthSession>(session);

        //upload each file
        long timeCost = System.currentTimeMillis();
        for (String file : fileList) {
            File f = new File(file);
            if (!f.exists()) {
                log.error("File(" + file + ") does not exist!");
                continue;
            }

            log.info("Uploading file " + file + " with size " + f.length() / 1024 + "KB");
            String dropboxFileName = file.startsWith("/") ? file : "/" + file;

            //try to get the old version of the file if exist
            Entry meta = null;
            try {
                meta = api.metadata(dropboxFileName, 1, null, false, null);
                if (meta != null) log.info("Old rev " + meta.rev);
            } catch (DropboxException e) {
                log.warn("File(" + file + ") may not exist in dropbox server!", e);
            }

            try {
                api.putFile(dropboxFileName, new FileInputStream(file), f.length(), meta == null ? null : meta.rev, null);
            } catch (Exception e) {
                log.error("Upload file(" + file + ") fail!", e);
                continue;
            }
            log.info("Upload file(" + file + ") success!");
        }
        timeCost = System.currentTimeMillis() - timeCost;
        log.info("Upload finished in " + timeCost + "ms !");
        return true;
    }

    @Override
    public Boolean call() throws Exception {
        return this.run();
    }
}
