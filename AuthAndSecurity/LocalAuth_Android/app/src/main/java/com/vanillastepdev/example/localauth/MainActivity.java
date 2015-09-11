package com.vanillastepdev.example.localauth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "LocalAuth-Sample";
   private RequestQueue mQueue;
   private ListView listView;

   String serverAddr = "http://192.168.205.118:8080";
   String serverAddrSecure = "https://192.168.205.118:8081";
   private ArrayAdapter adapter;
   private EditText userId;
   private EditText userPassword;
   private EditText newTalk;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mQueue = Volley.newRequestQueue(this);

      listView = (ListView)findViewById(R.id.listView);
      adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
      listView.setAdapter(adapter);

      userId = (EditText) findViewById(R.id.userId);
      userPassword = (EditText) findViewById(R.id.userPassword);
      newTalk = (EditText)findViewById(R.id.newTalk);

      java.net.CookieManager cookieManager = new java.net.CookieManager();
      cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
      CookieHandler.setDefault(cookieManager);
   }

   @Override
   protected void onResume() {
      super.onResume();
      refresh(null);
   }

   public void login(View v) {
      String url = serverAddr + "/login";


      StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            Log.d(TAG, "Response : " + response);
            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "LoginRequest Error", error);
            if ( error.networkResponse != null ) {
               Log.d(TAG, "Status Code : " + error.networkResponse.statusCode);
            }

            Toast.makeText(MainActivity.this, "LoginFail", Toast.LENGTH_SHORT).show();
         }
      }) {
         @Override
         protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("username", userId.getText().toString());
            params.put("password", userPassword.getText().toString());
            return params;
         }
      };

      mQueue.add(req);
   }

   public void composeNewTalk(View v) {
      String url = serverAddr + "/talks";
      final String talk = newTalk.getText().toString();
      Log.d(TAG, "Compose New Talk " + url + " talk : " + talk);
      StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {
            Log.d(TAG, "Success : " + response);
            refresh(null);
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, "새 글쓰기 에러", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Post New Talk Error " + error.networkResponse.statusCode, error);
         }
      }) {
         @Override
         protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("talk", talk);
            return params;
         }
      };
      mQueue.add(request);
   }

   public void refresh(View v) {
      String url = serverAddr + "/talks";
      Log.d(TAG, "Refresh : " + url);
      adapter.clear();
      JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
         @Override
         public void onResponse(JSONObject response) {
            try {
               JSONArray talks = response.getJSONArray("talks");
               for(int i = 0 ; i < talks.length() ; i++ ) {
                  String talk = talks.getString(i);
                  adapter.add(talk);
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
            Log.e(TAG, "Error", error);
         }
      });
      mQueue.add(request);
   }
}
