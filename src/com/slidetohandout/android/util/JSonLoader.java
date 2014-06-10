package com.slidetohandout.android.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.facebook.Request;

import android.os.AsyncTask;

public class JSonLoader extends AsyncTask<String, Integer, String> {
    String data = null;
    
    @Override
    protected String doInBackground(String... params) {
/*
        URL url = new URL(params[0]);
        
        // Creating an http connection to communicate with url 
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod();
        // Connecting to url 
        urlConnection.connect();
        
        // Reading data from url 
        iStream = urlConnection.getInputStream();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
        
        StringBuffer sb  = new StringBuffer();
        
        String line = "";
        while( ( line = br.readLine())  != null){
            sb.append(line);
        }
        
        data = sb.toString();
        
        br.close();
*/
        return null;

    }
    
    
}
