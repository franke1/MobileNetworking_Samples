package com.vanillastepdev.example.shoppingmall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

   static final public String APP_KEY = "API-KEY";
   private static final String TAG = "OpenAPI-Sample";
   private ListView mListView;
   private EditText mKeywordInput;
   private RequestQueue mQueue;
   private ArrayAdapter<String> mAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mListView = (ListView) findViewById(R.id.listView);

      mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
      mAdapter.add("상품 검색 결과를 출력하세요.");

      mListView.setAdapter(mAdapter);
      mKeywordInput = (EditText) findViewById(R.id.editText);

      Button searchButton = (Button)findViewById(R.id.searchButton);
      searchButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String keyword = mKeywordInput.getText().toString();
            searchProduct(keyword);
         }
      });

      mQueue = Volley.newRequestQueue(this);
   }

   private void searchProduct(String keyword) {
      try {
         // URLEncoder는 URL을 구성하는 요소(://) 도 인코딩하므로, 인코딩이 필요한 부분만 인코딩한다.
         String encoded = URLEncoder.encode(keyword, "UTF-8");
         String url = "http://apis.skplanetx.com/11st/common/products?version=1&format=json&appKey=" + APP_KEY + "&searchKeyword=" + encoded;
         Log.d(TAG, "url : " + url);

         JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               Log.d(TAG, "Response : " + response);

            }
         }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Log.e(TAG, "Error : " + error.getLocalizedMessage());
            }
         });
         mQueue.add(request);

      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
   }




}
