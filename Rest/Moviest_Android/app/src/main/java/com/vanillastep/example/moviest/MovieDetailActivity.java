package com.vanillastep.example.moviest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MovieDetailActivity extends AppCompatActivity {

   private static final String TAG = "MovieDetail";
   private RequestQueue mQueue;
   private TextView mMovieInfo;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_movie_detail);

      mMovieInfo = (TextView) findViewById(R.id.movieInfo);

      mQueue = RequestQueueSingleton.getInstance(this).getRequestQueue();

   }

   @Override
   protected void onResume() {
      super.onResume();
      Log.d(TAG, "Server Address : " + MainActivity.SERVER_ADDRESS);
      resolveMovieDetail();
   }

   void resolveMovieDetail() {
      try {
         String movieId = getIntent().getStringExtra("movieId");
         String encoded = URLEncoder.encode(movieId, "UTF-8");
         String url = MainActivity.SERVER_ADDRESS + "/movies/" + encoded;
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
         mQueue.add(request);
      } catch (Exception e) {
         Log.e(TAG, "Exception", e);
         e.printStackTrace();
      }

   }
}
