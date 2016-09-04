package com.wannabewize.example.httplibrary_okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "OKHttpExample";
    private TextView textView;
    private ImageView imageView;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.showImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Started downloading");
                String catImage = "http://lorempixel.com/720/1080/cats/"; // Too slow
                String screamImage = " http://www.ibiblio.org/wm/paint/auth/munch/munch.scream.jpg";
                new NetworkTask().execute(screamImage);
            }
        });

        // 타임아웃 30초
        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    private class NetworkTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected void onPreExecute() {
            // 준비 동작 : 비동기 동작 전에 실행, 메인 쓰레드에서 실행
            imageView.setImageBitmap(null);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String urlStr = params[0];

            Request request = new Request.Builder().url(urlStr).get().build();

            Call call = httpClient.newCall(request);
            try {
                Response response = call.execute();
                if ( response.isSuccessful() ) {
                    Log.d(TAG, "Response Success " + response.code());
//                    response.body()
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    return bitmap;
                }
                else {
                    Log.e(TAG, "Network Error : " + response.code() + ", " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // 백그라운드 동작 완료 : 메인 쓰레드에서 실행
            imageView.setImageBitmap(bitmap);
        }
    }
}
