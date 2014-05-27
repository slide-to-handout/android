package com.slidetohandout.android;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.slidetohandout.android.util.NetworkFileDownloader;
import com.slidetohandout.android.util.SocketIOUtil;
import com.slidetohandout.android.util.SocketIOUtil.OnGetSlideListener;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SocketIOUtil.getInstance().setOnGetSlideListener(new OnGetSlideListener() {
            @Override
            public void on(String downloadUrl) {
                open(downloadUrl);
            }
        });
        SocketIOUtil.getInstance().socketConnect();
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
}
