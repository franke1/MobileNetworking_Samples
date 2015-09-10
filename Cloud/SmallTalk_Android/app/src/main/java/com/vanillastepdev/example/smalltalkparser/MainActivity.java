package com.vanillastepdev.example.smalltalkparser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static final String TAG = "SmallTalk_Example";
    private static final int NEW_TALK_ACTIVITY = 1;
    private TalkAdapter adapter;
    private List<ParseObject> talks = new ArrayList<>();
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parse SDK 초기화
        Parse.enableLocalDatastore(this);
        // Parse.com에서 앱 콘솔 -> Setting -> Application ID와 Client Key
        Parse.initialize(this, "== APPLICATION ID ==", "== CLIENT KEY ==");

        RequestQueue queue = Volley.newRequestQueue(this);
        ImageCache cache = new ImageCache(10 * 1000 * 1000);
        imageLoader = new ImageLoader(queue, cache);
        ListView listView = (ListView)findViewById(R.id.talkList);
        adapter = new TalkAdapter();
        listView.setAdapter(adapter);
    }

    // Memory Cache
    class ImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

        public ImageCache(int maxSize) {
            super(maxSize);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 새로운 글로 리프레쉬
        refreshTalks(null);
    }

    // 새 글쓰기 버튼 리스너
    public void composeTalk(View v) {
        Intent intent = new Intent(MainActivity.this, ComposeActivity.class);
        startActivityForResult(intent, NEW_TALK_ACTIVITY);
    }

    // Refresh 버튼 리스너
    public void refreshTalks(View v) {
        // 이전 목록 지우기
        talks.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("talks");
        // talks 얻어오기
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + results.size() + " results");
                    talks = results;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    Handler handler = new Handler();

    class TalkAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return talks.size();
        }

        @Override
        public Object getItem(int i) {
            return talks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if ( view == null ) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.talk_layout, viewGroup, false);
            }

            TextView textView = (TextView) view.findViewById(R.id.textView);

            ParseObject item = (ParseObject) getItem(i);
            textView.setText(item.getString("text"));

            ParseFile imageFile = item.getParseFile("image");
            if ( imageFile != null ) {
                Log.d(TAG, "image url : " + imageFile.getUrl());
                final NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.imageView);
                imageView.setImageUrl(imageFile.getUrl(), imageLoader);

                // 부하가 크다.
//                imageFile.getDataInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] bytes, ParseException e) {
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        imageView.setImageBitmap(bitmap);
//                    }
//                });
            }

            return view;
        }
    }
}

