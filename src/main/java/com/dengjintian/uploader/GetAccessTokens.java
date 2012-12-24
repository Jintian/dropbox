package com.dengjintian.uploader;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This program is used to retrieve access token pair from dropbox server. <br/>
 * Steps:
 * <p>
 * 1. Go to https://www.dropbox.com/developers/apps, create an app and get the app key pair.
 * 2. Run this program, authenticate the app and get the access token pair from console output.
 * </p>
 */
public class GetAccessTokens {

    private static final String APP_KEY = "ob14gwp3yze7rs1";
    private static final String APP_SECRET = "4j3q1tutllfurqn";

    public static void main(String[] args) throws DropboxException, MalformedURLException, IOException, URISyntaxException {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        WebAuthSession session = new WebAuthSession(appKeys, AccessType.DROPBOX);
        WebAuthInfo authInfo = session.getAuthInfo();

        RequestTokenPair pair = authInfo.requestTokenPair;
        String url = authInfo.url;

        Desktop.getDesktop().browse(new URL(url).toURI());
        JOptionPane.showMessageDialog(null, "Press ok to continue once you have authenticated.");
        session.retrieveWebAccessToken(pair);

        AccessTokenPair tokens = session.getAccessTokenPair();
        System.out.println("Use this token pair in future so you don't have to re-authenticate each time:");
        System.out.println("Key token: " + tokens.key);
        System.out.println("Secret token: " + tokens.secret);

        DropboxAPI<WebAuthSession> api = new DropboxAPI<WebAuthSession>(session);

    }
}
