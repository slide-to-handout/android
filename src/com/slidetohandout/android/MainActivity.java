package com.slidetohandout.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

import com.slidetohandout.android.util.NetworkFileDownloader;

public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";

	private int page = 1;
	private PDFFile pdfFile = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button prevBtn = (Button) findViewById(R.id.button1);
		prevBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				load(page - 1);
			}
		});
		Button nextBtn = (Button) findViewById(R.id.button2);
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				load(page + 1);
			}
		});
		
		NetworkFileDownloader.OnDownloadListener listener = new NetworkFileDownloader.OnDownloadListener() {
			@Override
			public void onSuccess(File f) {
				// TODO Auto-generated method stub
				try {
					RandomAccessFile raf = new RandomAccessFile(f, "r");
					FileChannel channel = raf.getChannel();
					MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
					ByteBuffer buf = ByteBuffer.NEW(map);
					pdfFile = new PDFFile(buf);
				} catch (FileNotFoundException e) {
					Log.e(MainActivity.TAG, "File not found", e);
				} catch (IOException e) {
					Log.e(MainActivity.TAG, "General IO Error", e);
				}
			}
			
			@Override
			public void onFailure(Exception e) {
				// pass
			}
		};
        
		PDFImage.sShowImages = true;
		PDFPaint.s_doAntiAlias = true;
		HardReference.sKeepCaches = false;
		
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

	public void load(int page) {
		if (pdfFile == null) {
			Log.e(MainActivity.TAG, "PDF is not loaded yet");
			return;
		}
		this.page = page;
		
		PDFPage pdfPage = pdfFile.getPage(1, true);
		RectF rect = new RectF(0, 0, (int) pdfPage.getWidth(), (int) pdfPage.getHeight());
		Bitmap image = pdfPage.getImage((int)pdfPage.getWidth(), (int)pdfPage.getHeight(), rect, true, true);
		
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setImageBitmap(image);
	}
}
