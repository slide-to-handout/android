package com.slidetohandout.android;

import android.os.Bundle;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.slidetohandout.android.util.SocketIOUtil;

public class AndroidPDFActivity extends MuPDFActivity {

    @Override
    public void createUI(Bundle savedInstanceState) {
        super.createUI(savedInstanceState);
        
        setOnPageListener(new OnPageListner() {
            @Override
            public void onPage(int page) {
                SocketIOUtil.getInstance().movePage(page + 1);
            }
            
        });
    }
}
