package com.vanillastepdev.example.basicpost_android;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

   static private final String TAG = "SimplePOST-Sample";
   static private final String serverAddress = "http://192.168.0.88:3000";

   private EditText mTitle;
   private EditText mDirector;
   private WebView mWebView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTitle = (EditText)findViewById(R.id.title);
      mDirector = (EditText)findViewById(R.id.director);
      mWebView = (WebView)findViewById(R.id.webView);

      // 웹뷰로 목록 출력
      // TODO: 에러 처리 필요
      mWebView.setWebViewClient(new WebViewClient());
   }

   @Override
   protected void onResume() {
      super.onResume();
      refreshList();
   }

   // 요청 보내기
   public void sendRequest(View v) {
      new NetworkThread().start();
   }

   // 웹뷰에 목록 출력
   void refreshList() {
      Log.d(TAG, "Refresh WebView");
      mWebView.loadUrl(serverAddress);
   }

   Handler handler = new Handler();

   class NetworkThread extends Thread {
      @Override
      public void run() {
         try {
            String title = mTitle.getText().toString();
            String director = mDirector.getText().toString();

            // 요청 보내기
            URL url = new URL(serverAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST Method
            conn.setRequestMethod("POST");

            // Body, URLEncoded
            StringBuffer buffer = new StringBuffer();
            buffer.append("title=").append(URLEncoder.encode(title, "UTF-8"));
            buffer.append("&");
            buffer.append("director=").append(URLEncoder.encode(director, "UTF-8"));
            String bodyStr = buffer.toString();

            // 요청 메세지 헤더
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(bodyStr.length()));
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(bodyStr.getBytes("UTF-8"));
            os.flush();
            os.close();

            // 응답 얻기
            int statusCode = conn.getResponseCode();
            Log.d(TAG, "Status Code : " + statusCode);

            handler.post(new Runnable() {
               @Override
               public void run() {
                  refreshList();
               }
            });

         } catch (Exception e) {
            Log.e(TAG, "Excpetion", e);
            e.printStackTrace();
         }

      }
   }
}
