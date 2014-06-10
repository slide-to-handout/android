package com.slidetohandout.android;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.Session;
import com.facebook.SessionState;
import com.slidetohandout.android.util.NetworkFileDownloader;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

        updateView();/*
        Intent intent = new Intent(MainActivity.this, LoginUsingLoginFragmentActivity.class);
        startActivity(intent);
        setContentView(R.layout.activity_main);
        /*
        SocketIOUtil.getInstance().setOnGetSlideListener(new OnGetSlideListener() {
            @Override
            public void on(String downloadUrl) {
                open(downloadUrl);
            }
        });
        SocketIOUtil.getInstance().socketConnect();*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            System.out.println("logined");
        } else {
            onClickLogin();
            System.out.println("not login");
        }
    }

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void open(String downloadUrl) {
        final MainActivity that = this;
        NetworkFileDownloader.OnDownloadListener listener = new NetworkFileDownloader.OnDownloadListener() {
            @Override
            public void onSuccess(File f) {
                Uri uri = Uri.parse(f.getAbsolutePath());
                Intent intent = new Intent(that, AndroidPDFActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {
                // pass
            }
        };

        URL url = null;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException e) {
            return;
        }

        new NetworkFileDownloader(this)
            .setOnDownloadListener(listener)
            .execute(url);
        
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //updateView();
        }
    }
}
