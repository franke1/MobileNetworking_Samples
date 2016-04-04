package com.vanillastep.example.photoupload_volleyextension;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import static com.navercorp.volleyextensions.volleyer.Volleyer.*;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "PhotoUpload-Sample";
   private static final String serverAddress = "http://192.168.0.129:3001";

   private RequestQueue mQueue;
   private EditText mTitle;
   private ImageView mImageView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTitle = (EditText)findViewById(R.id.title);
      mImageView = (ImageView)findViewById(R.id.imageView);

      findViewById(R.id.uploadButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            upload();
         }
      });

      findViewById(R.id.showListButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serverAddress));
            startActivity(intent);
         }
      });

      findViewById(R.id.imageSelectButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectImage();
         }
      });

      mQueue = Volley.newRequestQueue(this);
   }

   void upload() {
      volleyer(mQueue)
              .post(serverAddress)
              .
   }

   void selectImage() {
      Drawable drawable = getResources().getDrawable(R.drawable.poster);
      Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
      mImageView.setImageBitmap(bitmap);
   }

   void showList(View v) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serverAddress));
      startActivity(intent);
   }
}
