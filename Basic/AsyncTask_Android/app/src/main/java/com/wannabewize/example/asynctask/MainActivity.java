package com.wannabewize.example.asynctask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AsyncTask";
    private TextView mTextView;
    private ImageView mImageView;
    private ImageTask mImageTask;
    private TextView mProgressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mProgressTextView = (TextView) findViewById(R.id.progressTextView);

        findViewById(R.id.textTaskButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://google.com";
                new TextTask().execute(url);

            }
        });

        findViewById(R.id.imageTaskButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageTask = new ImageTask();
                mImageTask.execute();
            }
        });

        findViewById(R.id.imageTaskCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mImageTask != null ) {
                    mImageTask.cancel(true);
                }
            }
        });
    }

    class ImageTask extends AsyncTask<Void, Integer, Bitmap> {
        private ProgressDialog mmProgressDialog;

        @Override
        protected void onPreExecute() {
            // 이미지 삭제
            mImageView.setImageBitmap(null);
            mTextView.setText(null);
            mProgressTextView.setText(null);

            // 텍스트 뷰 감추기
            mTextView.setVisibility(View.GONE);
            // 이미지 뷰 보이기
            mImageView.setVisibility(View.VISIBLE);

            mmProgressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_SPINNER);
            mmProgressDialog.setMessage("이미지 로딩 중");
            mmProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                // execute에서 전달받지 않는다
                URL url = new URL("http://www.ibiblio.org/wm/paint/auth/munch/munch.scream.jpg");
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                Bitmap result = BitmapFactory.decodeStream(is);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "취소");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mmProgressDialog.dismiss();
            if ( bitmap != null ) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    class TextTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "AsyncTASK 실행 준비");
            // 이미지 삭제
            mImageView.setImageBitmap(null);
            mTextView.setText(null);
            mProgressTextView.setText(null);

            mTextView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String urlStr = params[0];
            Log.d(TAG, "리소스 다운로드 : " + urlStr);
            StringBuffer buffer = new StringBuffer();
            try {
                URL url = new URL(urlStr);
                InputStream is = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ( (line = reader.readLine()) != null ) {
                    buffer.append(line);
                    publishProgress(buffer.length());
                }
                return buffer.toString();
            } catch (Exception e) {
                Log.e(TAG, "비동기 태스크 에러 : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressTextView.setText(values[0] + " bytes 로딩");
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "태스크 실행 끝 : " + s);
            if ( s != null ) {
                mTextView.setText(s);
            }
            else {
                mTextView.setText("Error");
            }

        }
    }
}
