package com.vanillastep.example.moviest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

   public static final String ServerAddress = "http://192.168.0.9:3000";

   private static final String TAG = "Moviest-Sample";
   private ArrayAdapter<String> adapter;
   private List<String> movieList = new ArrayList<>();
   private RequestQueue queue;
   private EditText mAddress;

   private OkHttpClient httpClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      ListView mListView = (ListView) findViewById(R.id.movieListView);
      adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movieList);
      mListView.setAdapter(adapter);

      findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            resolveMovieList();
//            resolveMovieList2();
         }
      });

      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            // TODO : 선택한 영화의 id를 전달한다
            intent.putExtra("movieId", "0");

            startActivity(intent);
         }
      });

      // Singleton 사용
      // https://developer.android.com/training/volley/requestqueue.html
//      queue = Volley.newRequestQueue(this);
      queue = RequestQueueSingleton.getInstance(this).getRequestQueue();

      // Singleton으로 OkHttpClient 얻기
      //httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();
      httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

   }

   @Override
   protected void onResume() {
      super.onResume();
      resolveMovieList();
//      resolveMovieList2();
   }

   public void composeNewMovie(View v) {
      Toast.makeText(MainActivity.this, "새 영화 정보 입력하기 - 구현 중", Toast.LENGTH_SHORT).show();
   }

   void resolveMovieList() {
      // OkHttp
      new MovieListTask().execute();
   }

   class MovieListTask extends AsyncTask<Void, Void, Void> {

      @Override
      protected void onPreExecute() {
         movieList.clear();
      }

      @Override
      protected Void doInBackground(Void... params) {
         try {
            String url = ServerAddress + "/movies";
            Request request = new Request.Builder().url(url).get().build();
            okhttp3.Response response = httpClient.newCall(request).execute();
            if ( response.isSuccessful() ) {
               String bodyStr = response.body().string();
               JSONObject root = new JSONObject(bodyStr);
               ArrayList<String> results = new ArrayList<>();

               JSONArray data = root.getJSONArray("data");
               for (int i = 0 ; i < data.length() ; i++ ) {
                  JSONObject movie = data.getJSONObject(i);
                  movieList.add(movie.getString("title"));
               }
            }
         } catch (Exception e) {
            Log.e(TAG, "Error : " + e.getMessage());
            e.printStackTrace();
         }
         return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
         adapter.notifyDataSetChanged();
      }
   }

   // Volley
   void resolveMovieList2() {
      movieList.clear();

      String url = ServerAddress + "/movies";

      JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
         @Override
         public void onResponse(JSONObject response) {
            try {
               Log.d(TAG, "Movie List : " + response);
               JSONArray movies = response.getJSONArray("movies");
               for ( int i = 0 ; i < movies.length() ; i++) {
                  JSONObject item = (JSONObject) movies.get(i);
                  String title = item.getString("title");
                  movieList.add(title);
               }

               adapter.notifyDataSetChanged();

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

   }
}