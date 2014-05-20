package com.slidetohandout.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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

import com.pdfjet.A4;
import com.pdfjet.Image;
import com.pdfjet.ImageType;
import com.pdfjet.PDF;
import com.pdfjet.Page;
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
			/*
			FileOutputStream fos = new FileOutputStream("http://whcarrot.iptime.org:8080/test.pdf");
	        PDF pdf = new PDF(fos);
	        InputStream f = getApplicationContext().getAssets().open("img0.jpg"); 
	        Image image = new Image(pdf, f, ImageType.JPG);
	        Page page = new Page(pdf, A4.PORTRAIT);
	        image.setPosition(0, 0);
	        image.drawOn(page);
	        pdf.flush();
	        fos.close();
*/
			//ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			//imageView.setImageBitmap(image);
	        
			PDFImage.sShowImages = true;
			PDFPaint.s_doAntiAlias = true;
			HardReference.sKeepCaches = false;
			
			File file = new File("http://whcarrot.iptime.org:8080/test.pdf");
			if(!file.exists()) {
				Log.i("whcarrot:file not exists", file.getAbsolutePath());
				return;
			}
		
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			
			FileChannel channel = raf.getChannel();
			MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			System.out.println(map.getChar(0));
			System.out.println(map.getChar(1));
			ByteBuffer buf = ByteBuffer.NEW(map);
			System.out.println(buf);
			pdfFile = new PDFFile(buf);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i("whcarrot:file", e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("whcarrot:io", e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
