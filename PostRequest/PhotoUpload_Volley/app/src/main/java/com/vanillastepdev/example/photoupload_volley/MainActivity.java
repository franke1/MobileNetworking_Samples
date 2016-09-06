package com.vanillastepdev.example.photoupload_volley;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "PhotoUpload-Sample";
   private static final String serverAddress = "http://192.168.0.45:3001";

   // 제목
   private EditText mTitle;
   // 이미지 뷰
   private ImageView mImageView;
   // 선택된 이미지
   private Uri mImageUri;

   private RequestQueue mQueue;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTitle = (EditText)findViewById(R.id.title);
      mImageView = (ImageView)findViewById(R.id.imageView);

      mQueue = Volley.newRequestQueue(this);
   }

   // 사진 고르기
   private static final int PICK_IMAGE_REQUEST = 1;
   public void selectImage(View v) {
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

   public void showList(View v) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serverAddress));
      startActivity(intent);
   }

   public void uploadContents(View v) {
      String title = mTitle.getText().toString();
      if ( title.length() < 1 ) {
         Toast.makeText(this, "글을 입력해주세요", Toast.LENGTH_SHORT).show();
         return;
      }

      MoviePosterUploadRequest req = new MoviePosterUploadRequest(Request.Method.POST, serverAddress, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            Log.d(TAG, "Response : " + response);
            Toast.makeText(MainActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
            mImageView.setImageBitmap(null);
            mTitle.setText("");
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "ErrorResponse", error);
         }
      });
      if ( mImageUri != null )
         req.addFileUpload("poster", mImageUri);
      req.addStringUpload("title", title);

      mQueue.add(req);
   }

   class MoviePosterUploadRequest extends StringRequest {

      public MoviePosterUploadRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
         super(method, url, listener, errorListener);
      }

      // 파일 업로드
      private Map<String, Uri> fileUploads = new HashMap<String, Uri>();

      // 키-밸류 업로드
      private Map<String,String> stringUploads = new HashMap<String,String>();


      public void addFileUpload(String param,Uri uri) {
         fileUploads.put(param, uri);
      }

      public void addStringUpload(String param,String content) {
         stringUploads.put(param,content);
      }

      final String boundary = "XXXYYYZZZ";
      final String lineEnd = "\r\n";

      @Override
      public byte[] getBody() throws AuthFailureError {
         Log.d(TAG, "getBody works in MultlpartRequest");
         try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);


            for ( String key : stringUploads.keySet() ) {
               dos.writeBytes("--" + boundary + lineEnd);
               dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
               dos.writeBytes("Content-Type: text/plain; charset-UTF-8" + lineEnd);
               dos.writeBytes(lineEnd);

               dos.writeBytes(stringUploads.get(key));
               dos.writeBytes(lineEnd);
            }

            for ( String key : fileUploads.keySet() ) {
               dos.writeBytes("--" + boundary + lineEnd);

               dos.writeBytes("Content-Disposition: form-data; name=\"" + key +"\"; filename=\"" + key + "\"" + lineEnd);
               dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
               dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
               dos.writeBytes(lineEnd);

               Uri uri = fileUploads.get(key);
               InputStream is = getContentResolver().openInputStream(uri);
               byte[] fileData = ByteStreams.toByteArray(is);
               dos.write(fileData);
               dos.writeBytes(lineEnd);
            }

            dos.writeBytes("--" + boundary + "--" + lineEnd);
            dos.flush();
            dos.close();

            return os.toByteArray();

         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }

      @Override
      public String getBodyContentType() {
         return "multipart/form-data; boundary=" + boundary;
      }
   }
}
