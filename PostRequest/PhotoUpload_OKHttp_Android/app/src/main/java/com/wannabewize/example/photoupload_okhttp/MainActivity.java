package com.wannabewize.example.photoupload_okhttp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PhotoUploadExample";
    // 서버 주소
    private static final String SERVER_ADDRESS = "http://192.168.0.129:3001/";
    // 제목
    private EditText mTitle;
    // 이미지 뷰
    private ImageView mImageView;
    // 선택된 이미지
    private Uri mImageUri;
    private OkHttpClient mHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = (EditText)findViewById(R.id.title);
        mImageView = (ImageView)findViewById(R.id.imageView);

        mHttpClient = new OkHttpClient.Builder().build();

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

        findViewById(R.id.uploadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadTask().execute();
            }
        });
    }

    private void showList() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SERVER_ADDRESS));
        startActivity(intent);
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

    class UploadTask extends AsyncTask<Void, Void, Boolean> {
        String title;
        String fileName;

        UploadTask() {
            title = mTitle.getText().toString();
            fileName = mImageUri.getLastPathSegment();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                InputStream is =  getContentResolver().openInputStream(mImageUri);
                // guava 라이브러리
                byte[] imageBytes = ByteStreams.toByteArray(is);

                String contentType = getContentResolver().getType(mImageUri);
//                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(contentType);

                Log.d(TAG, "ContentType : " + contentType);

                RequestBody imageBody = RequestBody.create(MediaType.parse(contentType), imageBytes);

                MultipartBody body = new MultipartBody.Builder()
                        .addFormDataPart("title", title)
                        .addFormDataPart("poster", fileName, imageBody).build();
                Request request = new Request.Builder()
                        .url(SERVER_ADDRESS)
                        .post(body).build();
                Response response = mHttpClient.newCall(request).execute();
                return response.isSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if ( success ) {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                mImageView.setImageDrawable(null);
                mTitle.setText("");
            }
            else {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
