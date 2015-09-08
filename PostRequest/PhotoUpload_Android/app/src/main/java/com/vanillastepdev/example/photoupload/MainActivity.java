package com.vanillastepdev.example.photoupload;

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

import com.google.common.io.ByteStreams;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CRL;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "PhotoUpload-Sample";

   // 서버 주소
   private EditText mAddress;
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
      mAddress = (EditText) findViewById(R.id.serverAddress);

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
      String url = mAddress.getText().toString();
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(intent);
   }

   public void uploadContents(View v) {
      String title = mTitle.getText().toString();
      if ( title.length() < 1 ) {
         Toast.makeText(this, "글을 입력해주세요", Toast.LENGTH_SHORT).show();
         return;
      }

      new NetworkThread().start();

   }

   static final String BOUNDARY = "multipart-part-devider";
   static final String CRLF = "\r\n";

   // POST 요청 - 데이터 키
   static final String FILE_KEY = "poster";
   static final String TITLE_KEY = "title";

   class NetworkThread extends Thread {
      @Override
      public void run() {
         try {

            String urlStr = mAddress.getText().toString();
            String title = mTitle.getText().toString();

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);


            // POST 바디 작성
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            // title - String
            dos.writeBytes("--" + BOUNDARY + CRLF);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + TITLE_KEY + "\"" + CRLF);
            dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + CRLF);
            dos.writeBytes(CRLF);

            // Part - Body
            dos.write(title.getBytes("UTF-8")); // UTF-8 인코딩
            dos.writeBytes(CRLF);

            // poster - File
            if ( mImageUri != null ) {
               String lastPath = mImageUri.getLastPathSegment();
//               Log.d(TAG, "lastPath : " + lastPath);

               dos.writeBytes("--" + BOUNDARY + CRLF);
               dos.writeBytes("Content-Disposition: form-data; name=\"" + FILE_KEY + "\"; filename=\"" + lastPath + "\"" + CRLF);
               dos.writeBytes("Content-Type: application/octet-stream" + CRLF);
               dos.writeBytes("Content-Transfer-Encoding: binary" + CRLF);
               dos.writeBytes(CRLF);

               // 파일
               InputStream fileIs = getContentResolver().openInputStream(mImageUri);
               byte[] fileBytes = ByteStreams.toByteArray(fileIs);
               dos.write(fileBytes);
               dos.writeBytes(CRLF);
               fileIs.close();
            }

            // 바디 마무리
            dos.writeBytes("--" + BOUNDARY + "--" + CRLF);
            dos.flush();
            dos.close();

            Log.d(TAG, "Response : " + conn.getResponseCode());

         }
         catch (Exception e) {
            Log.e(TAG, "Exception", e);
         }
      }
   }


}
