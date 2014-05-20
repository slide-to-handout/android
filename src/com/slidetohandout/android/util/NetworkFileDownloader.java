package com.slidetohandout.android.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkFileDownloader extends AsyncTask<URL, Void, File> {
    private final static String TAG = "NetworkFileDownloader";

    private final Context context;
    private OnDownloadListener listener;
    private Exception exception;

    public interface OnDownloadListener {
        void onSuccess(File f);
        void onFailure(Exception e);
    }

    public NetworkFileDownloader(Context context) {
        this.context = context;
        this.exception = null;
    }

    public NetworkFileDownloader setOnDownloadListener(OnDownloadListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected File doInBackground(URL... params) {
        final URL url = params[0];
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            // network failure
            exception = e;
            return null;
        } catch (ClassCastException e) {
            // invalid url
            exception = e;
            return null;
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.wtf(TAG, e);
            exception = e;
            return null;
        }
        String filename = "temp.pdf";
        File f = new File(context.getExternalCacheDir(), filename);
        InputStream is = null;
        FileOutputStream fs = null;
        try {
            is = conn.getInputStream();
            fs = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) > 0) {
                fs.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to download", e);
            exception = e;
            return null;
        } finally {
            closeSilently(fs);
            closeSilently(is);
        }
        return f;
    }

    private void closeSilently(Closeable c) {
        if (c == null) { return; }
        try {
            c.close();
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    protected void onPostExecute(File result) {
        if (listener != null) {
            if (result != null) {
                listener.onSuccess(result);
            } else {
                listener.onFailure(exception);
            }
        }
    }
}