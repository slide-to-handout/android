package com.slidetohandout.android;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.slidetohandout.android.util.NetworkFileDownloader;
import com.slidetohandout.android.util.SocketIOUtil;
import com.slidetohandout.android.util.SocketIOUtil.OnGetSlideListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class SearchChannelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent rIntent = getIntent();
        //String loginUrl = "http://slideout.krois.se/login";
        
        
        System.out.println(rIntent.getStringExtra("uid"));

        SocketIOUtil.getInstance().setOnGetSlideListener(new OnGetSlideListener() {
            @Override
            public void on(String downloadUrl) {
                open(downloadUrl);
            }
        });
        SocketIOUtil.getInstance().socketConnect();
    }
    
    public void open(String downloadUrl) {
        NetworkFileDownloader.OnDownloadListener listener = new NetworkFileDownloader.OnDownloadListener() {
            @Override
            public void onSuccess(File f) {
                Uri uri = Uri.parse(f.getAbsolutePath());
                Intent intent = new Intent(SearchChannelActivity.this, AndroidPDFActivity.class);
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
