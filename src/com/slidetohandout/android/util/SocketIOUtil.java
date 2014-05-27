package com.slidetohandout.android.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.slidetohandout.android.AndroidPDFActivity;
import com.slidetohandout.android.MainActivity;

public class SocketIOUtil {
    private static SocketIOUtil instance = null;
    
    private SocketIOUtil() { }
    
    public static synchronized SocketIOUtil getInstance() {
        if(instance == null) {
            instance = new SocketIOUtil();
        }
        
        return instance;
    }
    

    private final static String TAG = "SocketIOUtil";
    private final static String HOST = "http://slideout.krois.se/";
    private final static int CONNECT_ID = 1;
    private final static int GET_SLIDE_ID = 2;
    private final static int MOVE_PAGE_ID = 3;
    
    private SocketIOClient sioClient;

    public static interface OnGetSlideListener {
        public void on(String downloadUrl);
    }
    private OnGetSlideListener onGetSlideListener;
    
    public void socketConnect() {
        Log.i(TAG, "try to socket connect server..." + HOST);
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), HOST, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                sioClient = client;
                
                connect();
                /*
                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        System.out.println(string);
                        // TODO Auto-generated method stub
                        
                    }
                });
                client.on("someEvent", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
                        System.out.println("args: " + arguments.toString());
                    }
                });*/
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        try {
                            int id = json.getInt("id");
                            switch(id) {
                            case CONNECT_ID:
                                getSlide();
                                break;
                            case GET_SLIDE_ID:
                                onGetSlide(json);
                                break;
                            case MOVE_PAGE_ID:
                                break;
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // TODO Auto-generated method stub
                        System.out.println("json: " + json.toString());
                        
                    }
                });
            }
        });
    }

    
    public void connect() {
        if(sioClient == null) {
            return ;
        }

        JSONObject json = new JSONObject();
        
        try {
            json.put("method", "connect");
            JSONObject params = new JSONObject();
            params.put("session", "test");
            json.put("params", params);
            json.put("id", CONNECT_ID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            return ;
        }

        Log.i(TAG, "try to connect server...[HOST=" + HOST + ", PARAMS=" + json.toString() + "]");
        
        sioClient.emit(json);
    }
    
    public void getSlide() {
        if(sioClient == null) {
            return ;
        }

        JSONObject json = new JSONObject();
        
        try {
            json.put("method", "get_slide");
            JSONObject params = new JSONObject();
            //params.put("session", "test");
            json.put("params", params);
            json.put("id", GET_SLIDE_ID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            return ;
        }

        Log.i(TAG, "try to get slide...[HOST=" + HOST + ", PARAMS=" + json.toString() + "]");
        
        sioClient.emit(json);
    }
    
    public void onGetSlide(JSONObject json) {
        JSONObject result;
        try {
            result = json.getJSONObject("result");
            String downloadUrl = result.getString("download_URL");
            if(onGetSlideListener != null) {
                onGetSlideListener.on(downloadUrl);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void movePage(int page) {
        if(sioClient == null) {
            return ;
        }

        JSONObject json = new JSONObject();
        
        try {
            json.put("method", "move_page");
            JSONObject params = new JSONObject();
            params.put("page", page);
            json.put("params", params);
            json.put("id", MOVE_PAGE_ID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            return ;
        }

        Log.i(TAG, "try to move page...[HOST=" + HOST + ", PARAMS=" + json.toString() + "]");
        
        sioClient.emit(json);
    }

    public OnGetSlideListener getOnGetSlideListener() {
        return onGetSlideListener;
    }

    public void setOnGetSlideListener(OnGetSlideListener onGetSlideListener) {
        this.onGetSlideListener = onGetSlideListener;
    }
    
    
}
