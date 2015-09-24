package com.vanillastep.example.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
   private ListView listView;
   private ArrayAdapter<String> adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // 레이아웃에 FB LoginButton을 사용하면, setContentView보다 먼저 초기화돼야 한다.
      FacebookSdk.sdkInitialize(getApplicationContext());
      setContentView(R.layout.activity_main);

      listView = (ListView)findViewById(R.id.listView);
      adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
      listView.setAdapter(adapter);


      Button feedButton = (Button) findViewById(R.id.showFeedButton);
      feedButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            showFeed();
         }
      });

      callbackManager = CallbackManager.Factory.create();

      // FB 로그인 버튼
      LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
      loginButton.setReadPermissions("email", "public_profile", "user_posts");

      LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(LoginResult loginResult) {
            // 토큰에서 정보 얻기
            AccessToken token = loginResult.getAccessToken();
            String userId = token.getUserId();
            String tokenStr = token.getToken();

            Log.d(TAG, "Token : " + token + "User ID : " + userId + "Token Str : " + tokenStr);

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
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      callbackManager.onActivityResult(requestCode, resultCode, data);
   }

   // Feed 읽기
   void showFeed() {
      AccessToken accessToken = AccessToken.getCurrentAccessToken();
      if ( accessToken == null ) {
         Toast.makeText(this, "AccessToken 없음. 다시 로그인", Toast.LENGTH_SHORT).show();
         return;
      }

      // 목록 초기화
      adapter.clear();

      // FB의 피드 읽기 요청
      GraphRequest request = GraphRequest.newMeRequest(accessToken,
              new GraphRequest.GraphJSONObjectCallback() {
                 @Override
                 public void onCompleted(JSONObject object, GraphResponse response) {
                    if ( object == null ) {
                       Log.d(TAG, "Complete Response : " + response);
                       return;
                    }
                    try {
                       JSONArray data = object.getJSONArray("data");
                       for (int i = 0 ; i < data.length() ; i++ ) {
                          JSONObject item = (JSONObject) data.get(i);
                          if ( item.has("message") ) {
                             String message = item.getString("message");
                             adapter.add(message);
                          }
                       }
                       adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                       e.printStackTrace();
                    }
                 }

              }
      );
      request.setGraphPath("/me/feed");
      request.executeAsync();
   }
}
