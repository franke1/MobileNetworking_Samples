package com.vanillastepdev.example.fbauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
   static final String serverAddress = "http://192.168.0.105:3000";

   private static final String TAG = "FBMobileAuth-Sample";
   private CallbackManager callbackManager;
   private RequestQueue mQueue;
   private ListView listView;
   private ArrayAdapter adapter;
   private AccessToken token;
   private TextView tokenTextView;
   private EditText newTalk;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // 레이아웃에 FB LoginButton을 사용하면, setContentView보다 먼저 초기화돼야 한다.
      FacebookSdk.sdkInitialize(getApplicationContext());

      setContentView(R.layout.activity_main);

      callbackManager = CallbackManager.Factory.create();

      // FB 로그인 버튼
      LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
      loginButton.setReadPermissions("email", "public_profile", "user_posts");

      LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(LoginResult loginResult) {
            // 토큰에서 정보 얻기
            token = loginResult.getAccessToken();
            String userId = token.getUserId();
            String tokenStr = token.getToken();

            Log.d(TAG, "Token : " + tokenStr + "User ID : " + userId + "Token Str : " + tokenStr);

            // 토큰 만료 기간
            Date expires = token.getExpires();
            Log.d(TAG, "Expires : " + expires.toString());
         }

         @Override
         public void onCancel() {
            // 사용자 인증 취소
            Log.d(TAG, "On Cancel");
         }

         @Override
         public void onError(FacebookException e) {
            // 에러
            Log.e(TAG, "on Error", e);
         }
      });


      // 토큰 등록 버튼과 이벤트 핸들러
      Button registerButton = (Button)findViewById(R.id.registTokenButton);
      registerButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (token != null) {
               Log.d(TAG, "서버에 토큰 등록");
               registerToken(token.getToken());
            } else {
               Toast.makeText(MainActivity.this, "토큰 없음", Toast.LENGTH_SHORT).show();
            }
         }
      });

      // 발급된 토큰 출력용
      tokenTextView = (TextView)findViewById(R.id.tokenView);

      // 목록
      listView = (ListView)findViewById(R.id.listView);
      adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
      listView.setAdapter(adapter);

      // 새글 입력용
      newTalk = (EditText)findViewById(R.id.newTalk);

      // Volley Queue
      mQueue = Volley.newRequestQueue(this);

      // 컨텐츠 다시 얻기
      Button refreshButton = (Button)findViewById(R.id.showTalkListButton);
      refreshButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            resolveTalks();
         }
      });

      // 새 글 쓰기 버튼
      Button composeButton = (Button) findViewById(R.id.writeTalkButton);
      composeButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            composeNewTalk();
         }
      });

      // 쿠키 매니저 설정
      java.net.CookieManager cookieManager = new java.net.CookieManager();
      cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
      CookieHandler.setDefault(cookieManager);
   }

   @Override
   protected void onResume() {
      super.onResume();

      // 저장된 토큰이 있으면 설정
      token = AccessToken.getCurrentAccessToken();
      if ( token != null ) {
         tokenTextView.setText(token.getToken());
         Log.d(TAG, "token : " + token.getToken());
      }
   }

   // 페북 권한 승인/로그인 전환 이후
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      callbackManager.onActivityResult(requestCode, resultCode, data);
   }

   // 토큰 등록
   void registerToken(final String accessToken) {
      String url = serverAddress + "/auth/facebook/token";

      Response.Listener<String> resListener = new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            Log.d(TAG, "Response : " + response);
            Toast.makeText(MainActivity.this, "토큰 등록 성공", Toast.LENGTH_SHORT).show();
         }
      };

      Response.ErrorListener errListner = new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error : " + error.getLocalizedMessage());
            Log.e(TAG, "status : " + error.networkResponse);
            Toast.makeText(MainActivity.this, "토큰 등록 실패", Toast.LENGTH_SHORT).show();
         }
      };

      final StringRequest req = new StringRequest(Request.Method.POST, url, resListener, errListner ) {
         // POST 요청 바디
         @Override
         protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> body = new HashMap<>();
            // 토큰은 access_token 이름으로 POST 바디로 전송
            body.put("access_token", accessToken);
            return body;
         }
      };

      mQueue.add(req);
   }

   // 새 글 쓰기
   void composeNewTalk() {
      String url = serverAddress + "/talks";
      final String talk = newTalk.getText().toString();

      Response.Listener resListener = new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            resolveTalks();
         }
      };

      Response.ErrorListener errListener = new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Post New Talk Error", error);
            NetworkResponse res = error.networkResponse;
            Toast.makeText(MainActivity.this, "글 쓰기 에러 : " + res.statusCode, Toast.LENGTH_SHORT).show();
         }
      };

      StringRequest request = new StringRequest(Request.Method.POST, url, resListener, errListener) {
         @Override
         protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            // 사용자 이름은 페이스북에서 얻어온다
            params.put("talk", talk);
            return params;
         }
      };
      mQueue.add(request);
   }

   void resolveTalks() {
      String url = serverAddress + "/talks";
      adapter.clear();
      JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
         @Override
         public void onResponse(JSONObject response) {
            try {
               Log.d(TAG, response.toString(3));
               JSONArray talks = response.getJSONArray("talks");
               for(int i = 0 ; i < talks.length() ; i++ ) {
                  JSONObject talk = (JSONObject) talks.get(i);
                  String text = talk.getString("talk");
                  String writer = talk.getString("writer");
                  adapter.add(text + "  by " + writer);
               }
            } catch (JSONException e) {
               Log.d(TAG, "JSON Exception", e);
               e.printStackTrace();
            }
            adapter.notifyDataSetChanged();

         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, "에러 : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error", error);
         }
      });
      mQueue.add(request);
   }
}
