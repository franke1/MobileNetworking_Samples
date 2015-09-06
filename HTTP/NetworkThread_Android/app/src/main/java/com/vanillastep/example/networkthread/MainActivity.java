package com.vanillastep.example.networkthread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "NetworkThread-Sample";
   private ImageView imageView;
   private TextView textView;

   // Random Placeholder Image
   String urlStr = "http://lorempixel.com/720/1080/cats/";

   Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         if ( msg.what == 1 ) {
            String text = (String) msg.obj;
            textView.setText(text);
         }
         else if ( msg.what == 2 ) {
            Bitmap bitmap = (Bitmap)msg.obj;
            imageView.setImageBitmap(bitmap);
         }
         else {
            Log.e(TAG, "Wrong Message");
         }
      }
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      imageView = (ImageView)findViewById(R.id.imageView);
      textView = (TextView)findViewById(R.id.textView);
   }

   public void showImage(View v) {
      // 이미지 지우기
      imageView.setImageBitmap(null);
      new NetworkThread().start();
   }

   class NetworkThread extends Thread {
      @Override
      public void run() {
         try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int code = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            Message message1 = new Message();
            message1.what = 1;
            message1.obj = "Response : " + code + " : " + responseMessage;
            handler.sendMessage(message1);

            InputStream is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            Message message2 = new Message();
            message2.what = 2;
            message2.obj = bitmap;
            handler.sendMessage(message2);

         } catch (Exception e) {
            Log.e(TAG, "Exception", e);
            e.printStackTrace();
         }
      }
   }

}
