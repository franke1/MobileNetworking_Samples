package com.vanillastepdev.example.apachehttpclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "ApacheLibrary-Sample";
   private ImageView imageView;
   private TextView textView;

   private HttpClient httpClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      httpClient = new DefaultHttpClient();

      imageView = (ImageView)findViewById(R.id.imageView);
      textView = (TextView)findViewById(R.id.textView);
   }

   Handler handler = new Handler();


   public void showImage(View v) {
      // 이미지 지우기
      imageView.setImageBitmap(null);
      new NetworkThread().start();
   }

   class NetworkThread extends Thread {
      @Override
      public void run() {
         try {
            // Random Placeholder Image
            String urlStr = "http://lorempixel.com/720/1080/cats/";

            HttpGet req = new HttpGet(urlStr);

            HttpResponse res = httpClient.execute(req);
            final String responseStr = res.getStatusLine().toString();

            InputStream is = res.getEntity().getContent();
            final Bitmap bitmap = BitmapFactory.decodeStream(is);

            handler.post(new Runnable() {
               @Override
               public void run() {
                  textView.setText(responseStr);
                  imageView.setImageBitmap(bitmap);
               }
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
