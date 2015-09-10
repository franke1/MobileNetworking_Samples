package com.vanillastep.example.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "Facebook-App";

   private CallbackManager callbackManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // 레이아웃에 FB LoginButton을 사용하면, setContentView보다 먼저 초기화돼야 한다.
      FacebookSdk.sdkInitialize(getApplicationContext());
      setContentView(R.layout.activity_main);

      callbackManager = CallbackManager.Factory.create();

      // 쿠키 매니저
      // TODO : 영구 저장 필요
//      java.net.CookieManager cookieManager = new java.net.CookieManager();
//      cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//      CookieHandler.setDefault(cookieManager);

      // FB 로그인 버튼
      LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

      loginButton.setReadPermissions("email", "public_profile");

      LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(LoginResult loginResult) {
            // 토큰에서 정보 얻기
            AccessToken token = loginResult.getAccessToken();
            String userId = token.getUserId();
            String tokenStr = token.getToken();

            Log.d(TAG, "Token : " + token);
            Log.d(TAG, "User ID : " + userId);
            Log.d(TAG, "Token Str : " + tokenStr);

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
            Log.e(TAG, "on Error",e);
         }
      });
   }

   @Override
   protected void onResume() {
      super.onResume();


   }

   public void showFeed(View v) {
      AccessToken accessToken = AccessToken.getCurrentAccessToken();
      if ( accessToken == null ) {
         Toast.makeText(this, "AccessToken 없음", Toast.LENGTH_SHORT).show();
         return;
      }

      GraphRequest request = GraphRequest.newMeRequest(accessToken,
              new GraphRequest.GraphJSONObjectCallback() {
                 @Override
                 public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                       JSONArray data = object.getJSONArray("data");
                       for (int i = 0 ; i < data.length() ; i++ ) {
                          JSONObject item = (JSONObject) data.get(i);
                          Log.d(TAG, item.getString("message"));
                       }
                    } catch (JSONException e) {
                       e.printStackTrace();
                    }
                 }

              });

      Bundle parameters = new Bundle();
      parameters.putString("fields", "message,picture,created_time");
      request.setParameters(parameters);
      request.executeAsync();
   }



   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      Log.d(TAG, "onActivityResult : requestCode " + requestCode + " resultCode : " + resultCode);
   }
}
