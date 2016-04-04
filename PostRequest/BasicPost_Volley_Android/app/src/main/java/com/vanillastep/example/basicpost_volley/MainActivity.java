package com.vanillastep.example.basicpost_volley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
   static private final String TAG = "SimplePOST-Sample";
   static private final String serverAddress = "http://192.168.0.129:3000";

   private EditText mTitle;
   private EditText mDirector;
   private WebView mWebView;
   private RequestQueue queue;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTitle = (EditText)findViewById(R.id.title);
      mDirector = (EditText)findViewById(R.id.director);
      mWebView = (WebView)findViewById(R.id.webView);

      queue = Volley.newRequestQueue(this);

      findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sendRequest();
//            sendRequest2();
         }
      });
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

   void sendRequest() {
      // 커스텀 요청 객체 생성
      MovieInfoRequest request = new MovieInfoRequest(Request.Method.POST, serverAddress, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            refreshList();
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error", error);
            NetworkResponse response = error.networkResponse;
            if ( response != null )
               Log.e(TAG, "Error Response : " + response.statusCode);
         }
      });
      request.setTitle(mTitle.getText().toString());
      request.setDirector(mDirector.getText().toString());
      queue.add(request);
   }

   // 상속을 이용한 요청 클래스 작성
   class MovieInfoRequest extends StringRequest {

      Map<String, String> params = new HashMap<>();

      public MovieInfoRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
         super(method, url, listener, errorListener);
      }

      public MovieInfoRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
         super(url, listener, errorListener);
      }

      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
         return params;
      }

      void setTitle(String title) {
         params.put("title", title);
      }

      void setDirector(String director) {
         params.put("director", director);
      }

      @Override
      public String getBodyContentType() {
         return "application/x-www-form-urlencoded; charset=UTF-8";
      }
   }

   // 요청 보내기
   public void sendRequest2() {
      StringRequest request = new StringRequest(Request.Method.POST, serverAddress, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            refreshList();
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error", error);
            NetworkResponse response = error.networkResponse;
            if ( response != null )
               Log.e(TAG, "Error Response : " + response.statusCode);
         }
      }){
         @Override
         protected Map<String, String> getParams() throws AuthFailureError {
            // 바디 작성
            String title = mTitle.getText().toString();
            String director = mDirector.getText().toString();

            Map<String, String> params = new HashMap<>();
            params.put("title", title);
            params.put("director", director);

            return params;
         }

         @Override
         public String getBodyContentType() {
            // 컨텐트 타입
            return "application/x-www-form-urlencoded; charset=UTF-8";
         }
      };
      queue.add(request);
   }
}
