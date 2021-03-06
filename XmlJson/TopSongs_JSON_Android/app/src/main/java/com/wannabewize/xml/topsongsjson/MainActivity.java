package com.wannabewize.xml.topsongsjson;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String urlStr = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/json";
    private static final String TAG = "JSONParing";

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> songList = new ArrayList<>();

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.songList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        parseTopSongs();
    }

    class JsonParser extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                JSONObject result = new JSONObject(buffer.toString());
                JSONObject feed = result.getJSONObject("feed");
                JSONArray entries = feed.getJSONArray("entry");
                for ( int i = 0 ; i < entries.length() ; i++ ) {
                    JSONObject entry = entries.getJSONObject(i);

                    String title = entry.getJSONObject("title").getString("label");
                    Log.d(TAG, "title : " + title);
                    songList.add(title);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Exception" , e);
                e.printStackTrace();
            }
        }
    };

    private void parseTopSongs() {
        songList.clear();
        new JsonParser().start();
    }
}
