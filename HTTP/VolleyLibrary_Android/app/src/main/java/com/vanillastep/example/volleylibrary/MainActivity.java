package com.vanillastep.example.volleylibrary;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "Volley-Sample";
   private ImageView imageResultView;
   private RequestQueue queue;
   private TextView textResultView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      imageResultView = (ImageView) findViewById(R.id.imageResult);
      textResultView = (TextView) findViewById(R.id.textResult);

      queue = Volley.newRequestQueue(this);

      findViewById(R.id.showImageButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sendImageRequest();
         }
      });

      findViewById(R.id.showTextButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sendTextRequest();
         }
      });
   }

   private void sendTextRequest() {
      textResultView.setVisibility(View.VISIBLE);
      textResultView.setText(null);
      imageResultView.setVisibility(View.GONE);

      String urlStr = "http://google.com";
      StringRequest request = new StringRequest(urlStr, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            textResultView.setText(response);
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            int statusCode = response.statusCode;

            Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception", error);
         }
      });
      queue.add(request);
   }

   private void sendImageRequest() {
      textResultView.setVisibility(View.GONE);
      imageResultView.setVisibility(View.VISIBLE);
      imageResultView.setImageBitmap(null);

      // Random Placeholder Image
      String urlStr = "http://lorempixel.com/720/1080/cats/";

      // 로딩 다이얼로그
      final ProgressDialog progress = new ProgressDialog(MainActivity.this);
      progress.setMessage("이미지 로딩");
      progress.show();


      ImageRequest request = new ImageRequest(urlStr, new Response.Listener<Bitmap>() {
         @Override
         public void onResponse(Bitmap response) {
            imageResultView.setImageBitmap(response);
            progress.dismiss();
         }
      }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888,
        new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
              Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
              progress.dismiss();
              Log.e(TAG, "Exception", error);
           }
        }
      );
      queue.add(request);
   }
}
