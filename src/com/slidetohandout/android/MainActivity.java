package com.slidetohandout.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

public class MainActivity extends Activity {
	private int page = 1;
	private PDFFile pdfFile;
	
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
		
		try {
			PDFImage.sShowImages = true;
			PDFPaint.s_doAntiAlias = true;
			HardReference.sKeepCaches = false;
			
			File file = new File("/storage/external_SD/test.pdf");
			if(!file.exists()) {
				Log.i("whcarrot:file not exists", file.getAbsolutePath());
				return;
			}
		
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
			
			pdfFile = new PDFFile(buf);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i("whcarrot:file", e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("whcarrot:io", e.toString());
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void load(int page) {
		this.page = page;
		
		PDFPage pdfPage = pdfFile.getPage(1, true);
		RectF rect = new RectF(0, 0, (int) pdfPage.getWidth(), (int) pdfPage.getHeight());
		Bitmap image = pdfPage.getImage((int)pdfPage.getWidth(), (int)pdfPage.getHeight(), rect, true, true);
		
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setImageBitmap(image);
	}
}
