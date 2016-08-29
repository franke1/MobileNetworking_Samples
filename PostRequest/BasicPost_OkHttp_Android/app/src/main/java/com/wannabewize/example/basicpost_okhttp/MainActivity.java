package com.wannabewize.example.basicpost_okhttp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static private final String TAG = "SimplePOST-Sample";
    static private final String serverAddress = "http://192.168.0.129:3000";

    private EditText mTitle;
    private EditText mDirector;
    private WebView mWebView;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = (EditText)findViewById(R.id.title);
        mDirector = (EditText)findViewById(R.id.director);
        mWebView = (WebView)findViewById(R.id.webView);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String director = mDirector.getText().toString();
                new MovieInfoPostTask(title, director).execute();
            }
        });

        httpClient = new OkHttpClient.Builder().build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    // 웹뷰에 목록 출력
    void refreshList() {
        Log.d(TAG, "Refresh WebView");
        mWebView.loadUrl(serverAddress);
    }

    class MovieInfoPostTask extends AsyncTask<Void, Void, Void> {
        private final String title;
        private final String director;

        MovieInfoPostTask(String title, String director) {
            this.title = title;
            this.director = director;
        }

        @Override
        protected Void doInBackground(Void... params) {

            // URLEncoded 방식의 메세지 바디 생성
            FormBody body = new FormBody.Builder()
                    .add("title", title)
                    .add("director", director)
                    .build();

            Request request = new Request.Builder().url(serverAddress).post(body).build();
            try {
                Response reponse = httpClient.newCall(request).execute();
                if ( reponse.isSuccessful() ) {
                    Log.d(TAG, "POST Success");
                }
                else {
                    Log.d(TAG, "Error : " + reponse.code() + ", " + reponse.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            refreshList();
        }
    }
}
