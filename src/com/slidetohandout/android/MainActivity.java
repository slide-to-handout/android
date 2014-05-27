package com.slidetohandout.android;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.slidetohandout.android.util.NetworkFileDownloader;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity that = this;
        
        NetworkFileDownloader.OnDownloadListener listener = new NetworkFileDownloader.OnDownloadListener() {
            @Override
            public void onSuccess(File f) {
                Uri uri = Uri.parse(f.getAbsolutePath());
                Intent intent = new Intent(that, MuPDFActivity.class);
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
            url = new URL("http://whcarrot.iptime.org:8080/test.pdf");
        } catch (MalformedURLException e) {
            return;
        }

        new NetworkFileDownloader(this)
            .setOnDownloadListener(listener)
            .execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
