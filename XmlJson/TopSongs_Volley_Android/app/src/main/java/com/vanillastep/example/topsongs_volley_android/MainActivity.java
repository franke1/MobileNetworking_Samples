package com.vanillastep.example.topsongs_volley_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


   private static final String TAG = "JSONParing";

   private ListView listView;
   private ArrayAdapter<String> adapter;
   private ArrayList<String> songList = new ArrayList<>();

   private RequestQueue mQueue;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      listView = (ListView) findViewById(R.id.songList);
      adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
      listView.setAdapter(adapter);

      mQueue = Volley.newRequestQueue(this);
   }

   @Override
   protected void onResume() {
      super.onResume();
      showTopSongs();
   }

   void showTopSongs() {
      String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/json";
      songList.clear();

      JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
         @Override
         public void onResponse(JSONObject response) {
            try {
               JSONObject feed = response.getJSONObject("feed");
               JSONArray entries = feed.getJSONArray("entry");
               for ( int i = 0 ; i < entries.length() ; i++ ) {
                  JSONObject entry = entries.getJSONObject(i);

                  String title = entry.getJSONObject("title").getString("label");
                  Log.d(TAG, "title : " + title);
                  songList.add(title);
               }

               adapter.notifyDataSetChanged();
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Response Error", error);
         }
      });
      mQueue.add(request);

   }


}
