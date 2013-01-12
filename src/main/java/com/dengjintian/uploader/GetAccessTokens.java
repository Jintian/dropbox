package com.dengjintian.uploader;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.*;

import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

/**
 * This program is used to retrieve access token pair from dropbox server. <br/>
 * Steps:
 * <p>
 * 1. Go to https://www.dropbox.com/developers/apps, create an app and get the app key pair. <br/>
 * 2. Run this program,
 * authenticate the app and get the access token pair from console output.
 * </p>
 */
public class GetAccessTokens {

    private static String APP_KEY;
    private static String APP_SECRET;

    public static void main(String[] args) throws DropboxException, MalformedURLException, IOException, URISyntaxException {
        setupAppKeyPair();
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        WebAuthSession session = new WebAuthSession(appKeys, AccessType.DROPBOX);
        WebAuthInfo authInfo = session.getAuthInfo();

        RequestTokenPair pair = authInfo.requestTokenPair;
        String url = authInfo.url;

        Desktop.getDesktop().browse(new URL(url).toURI());
        JOptionPane.showMessageDialog(null, "Press ok to continue once you have authenticated.");
        try {
            session.retrieveWebAccessToken(pair);
        } catch (Exception e) {
            System.out.println("authentication fail with exception:" + e);
        }

        AccessTokenPair tokens = session.getAccessTokenPair();
        System.out.println("Use this token pair in future so you don't have to re-authenticate each time:");
        System.out.println("accessKey: " + tokens.key);
        System.out.println("accessSecret: " + tokens.secret);
        System.exit(0);
    }

    private static void setupAppKeyPair() {
        APP_KEY = System.getProperty("appKey");
        APP_SECRET = System.getProperty("appSecret");
    }
}
