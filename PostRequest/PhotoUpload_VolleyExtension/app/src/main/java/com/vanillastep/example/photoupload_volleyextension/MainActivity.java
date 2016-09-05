package com.vanillastep.example.photoupload_volleyextension;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileDescriptor;
import java.net.URI;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "PhotoUpload-Sample";
   private static final String serverAddress = "http://192.168.0.30:3001";

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

      try {
         volleyer(mQueue)
                 .post(serverAddress)
                 .addStringPart("title", title)
                 .addFilePart("poster", image)
                 .withListener(listner)
                 .withErrorListener(errorListener)
                 .execute()


         AssetFileDescriptor afd = getAssets().openFd("poster.jpeg");
         Log.d(TAG, "AssetFileDescriptor : " + afd.toString());
         FileDescriptor fd = afd.getFileDescriptor();
         Log.d(TAG, "FileDescriptor : " + fd.toString());
         String assetPath = "file:///android_asset/poster.jpeg";

         URI uri = new URI(assetPath);
         Log.d(TAG, "URI : " + uri);
         File f = new File(uri);

         Log.d(TAG, "asset file exists : " +  f.exists() + " path : " + f.getAbsolutePath());

      } catch (Exception e) {
         e.printStackTrace();
      }


//      volleyer(mQueue)
//              .post(serverAddress)
//              .addHeader("Content-Type", "multipart/form-data")
//              .addStringPart("title","그림 제목")
   }

   void selectImage() {
   }

   void showList(View v) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serverAddress));
      startActivity(intent);
   }
}
