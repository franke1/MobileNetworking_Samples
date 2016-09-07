package com.vanillastep.example.moviest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class MovieDetailActivity extends AppCompatActivity {

   private static final String TAG = "MovieDetail";
   private RequestQueue queue;
   private TextView mMovieInfo;
   private OkHttpClient httpClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_movie_detail);

      mMovieInfo = (TextView) findViewById(R.id.movieInfo);


      // Singleton으로 Volley의 큐 얻기
      queue = RequestQueueSingleton.getInstance(this).getRequestQueue();

      // Singleton으로 OkHttp의 HttpClient 얻기
      httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
   }

   @Override
   protected void onResume() {
      super.onResume();
      resolveMovieDetail();
   }

   void resolveMovieDetail() {
      try {
         String movieId = getIntent().getStringExtra("movieId");

         String url = MainActivity.ServerAddress + "/movies/" + movieId;
         Log.d(TAG, "MovieDetail : " + url);

         JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               try {
                  mMovieInfo.setText(response.toString(4));
               } catch (JSONException e) {
                  Log.e(TAG, "JSONException", e);
                  e.printStackTrace();
               }
            }
         }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
         });
         queue.add(request);
      } catch (Exception e) {
         Log.e(TAG, "Exception", e);
         e.printStackTrace();
      }

   }
}
