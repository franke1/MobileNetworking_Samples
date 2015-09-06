package com.vanillastep.example.volleylibrary;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "Volley-Sample";
   private TextView textView;
   private ImageView imageView;
   private RequestQueue queue;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      textView = (TextView) findViewById(R.id.textView);
      imageView = (ImageView) findViewById(R.id.imageView);

      queue = Volley.newRequestQueue(this);
   }

   public void showImage(View v) {
      // Random Placeholder Image
      String urlStr = "http://lorempixel.com/720/1080/cats/";

      imageView.setImageBitmap(null);

      ImageRequest request = new ImageRequest(urlStr, new Response.Listener<Bitmap>() {
         @Override
         public void onResponse(Bitmap response) {
            imageView.setImageBitmap(response);
         }
      }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888,
        new Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError error) {
              Log.e(TAG, "Exception", error);
           }
        }
      );
      queue.add(request);
   }
}
