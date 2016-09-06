package com.vanillastep.example.photoupload_volleyextension;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.navercorp.volleyextensions.volleyer.factory.DefaultRequestQueueFactory;
import com.navercorp.volleyextensions.volleyer.factory.HttpStackFactory;
import com.navercorp.volleyextensions.volleyer.multipart.stack.DefaultMultipartHttpStack;
import com.navercorp.volleyextensions.volleyer.multipart.stack.MultipartHttpStack;
import com.navercorp.volleyextensions.volleyer.multipart.stack.MultipartHttpStackWrapper;

import java.io.File;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "PhotoUpload-Sample";
   private static final String serverAddress = "http://192.168.0.45:3001";

   private RequestQueue mQueue;
   // 제목
   private EditText mTitle;
   // 이미지 뷰
   private ImageView mImageView;
   // 선택된 이미지
   private Uri mImageUri;

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

      findViewById(R.id.imageSelectButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectImage();
         }
      });

      findViewById(R.id.showListButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            showList();
         }
      });

      MultipartHttpStack multipartStack = new DefaultMultipartHttpStack();
      mQueue = Volley.newRequestQueue(this, multipartStack);
   }

   void upload() {
      String title = mTitle.getText().toString();
      Log.d(TAG, "Title : " + title);

      // TODO : File 객체 만들기
      if ( mImageUri != null ) {
         File file = new File(mImageUri.toString());
         Log.d(TAG, "Image File : " + mImageUri.toString() + " isFile : " + file.isFile());
      }

      try {

         // TODO : volleyer로 생성되는 바디 메세지는 서버에서 에러 발생
         volleyer(mQueue)
                 .post(serverAddress)
                 .addStringPart("title", title)
                 .withListener(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Success");
                    }
                 })
                 .withErrorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error : ", error);
                    }
                 })
                 .execute();

      } catch (Exception e) {
         Log.e(TAG, "Exception ", e);
         e.printStackTrace();
      }
   }

   // 사진 고르기
   private static final int PICK_IMAGE_REQUEST = 1;
   private void selectImage() {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("image/*");
      startActivityForResult(intent, PICK_IMAGE_REQUEST);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      Log.d(TAG, "result Code : " + resultCode);

      if ( resultCode != Activity.RESULT_OK ) {
         Toast.makeText(this, "file choose cancelled", Toast.LENGTH_SHORT).show();
         Log.d(TAG, "file choose cancelled");
         return;
      }

      if ( PICK_IMAGE_REQUEST == requestCode ) {
         try {
            Log.d(TAG, "data : " + data);
            mImageUri = data.getData();
            // 화면에 이미지 출력
            mImageView.setImageURI(mImageUri);
         } catch (Exception e) {
            Log.e(TAG, "URISyntaxException", e);
            e.printStackTrace();
         }
      }
   }

   void showList() {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serverAddress));
      startActivity(intent);
   }
}
