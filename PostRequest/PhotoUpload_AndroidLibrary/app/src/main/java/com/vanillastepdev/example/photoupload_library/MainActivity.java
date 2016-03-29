package com.vanillastepdev.example.photoupload_library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public class MainActivity extends AppCompatActivity {

   private static final int PICK_IMAGE_REQUEST = 1;
   private static final String TAG = "ImageUpload-Sample";
   private EditText mTitle;
   private ImageView mImageView;
   private EditText mAddress;
   private Uri mImageUri;
   private AsyncHttpClient client;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTitle = (EditText)findViewById(R.id.title);
      mImageView = (ImageView)findViewById(R.id.imageView);
      mAddress = (EditText) findViewById(R.id.serverAddress);

      Button selectButton = (Button)findViewById(R.id.imageSelectButton);
      selectButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
         }
      });

      Button showListButton = (Button)findViewById(R.id.showListButton);
      showListButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String url = mAddress.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
         }
      });

      Button uploadButton = (Button)findViewById(R.id.uploadButton);
      uploadButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            uploadContent();
         }
      });

      client = new AsyncHttpClient();
   }

   void uploadContent() {
      String title = mTitle.getText().toString();
      if ( title.length() < 1 ) {
         Toast.makeText(this, "글을 입력해주세요", Toast.LENGTH_SHORT).show();
         return;
      }

      if ( mImageUri == null ) {
         Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
         return;
      }


      RequestParams params = new RequestParams();
      params.add("title", title);

      InputStream is = null;
      try {
         is = getContentResolver().openInputStream(mImageUri);
         params.put("poster", is, "poster.jpg");
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      String url = mAddress.getText().toString();
      client.post(url, params, new AsyncHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            StringBuilder sb = new StringBuilder();
            sb.append(responseBody);
            Log.d(TAG, "onSuccess : " + sb.toString());
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            StringBuilder sb = new StringBuilder();
            sb.append(responseBody);
            Log.d(TAG, "Fail : status " + statusCode + " response : " + sb.toString());
         }
      });
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if ( PICK_IMAGE_REQUEST == requestCode ) {
         if ( resultCode != Activity.RESULT_OK ) {
            Log.d(TAG, "Image file select cancel");
            return;
         }

         mImageUri = data.getData();
         // 선택한 이미지를 이미지 뷰로 출력
         mImageView.setImageURI(mImageUri);
      }
   }
}
