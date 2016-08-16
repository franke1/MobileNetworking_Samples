package com.vanillastepdev.example.cookies;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CookiesExample";
    private static final String SERVICE_ADDRESS = "http://192.168.10.161:3000";
    private OkHttpClient client;
    private TextView resultView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = (TextView)findViewById(R.id.resultView);
        handler = new Handler();

        // For Persistent Cookie - https://github.com/franmontiel/PersistentCookieJar
        CookieHandler cookieHandler = new CookieManager();
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieHandler);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyThread().start();
            }
        });
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            Request request = new Request.Builder().url(SERVICE_ADDRESS).build();
            try {
                Response response = client.newCall(request).execute();
                String bodyStr = response.body().string();
                if ( bodyStr != null ) {
                    JSONObject root = new JSONObject(bodyStr);
                    final String visit = root.getString("sessionVisit");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultView.setText("visit : " + visit);
                        }
                    });
                }
                else {
                    Log.d(TAG, "Response body error");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
            }
        }
    }
}
