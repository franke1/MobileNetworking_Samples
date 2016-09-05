package com.vanillastepdev.example.imageloader_asynctask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AsyncImageLoader";
    private ImageView mImageView;
    private String image = "http://www.ibiblio.org/wm/paint/auth/munch/munch.scream.jpg";

    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);

        findViewById(R.id.loadImageButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(null);
                new ImageLoadingTask(image).execute();
            }
        });

        mMemoryCache = new LruCache<String, Bitmap>(200 * 1000); // 200kb(이미지 크기는 150kb)

        findViewById(R.id.loadImageButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(null);

                // 캐시 먼저 찾기
                Bitmap bitmap = mMemoryCache.get(image);
                if ( bitmap == null ) {
                    new ImageLoadingTask2(image).execute();
                }
                else {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });

        findViewById(R.id.clearCache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캐시 비우기
                mMemoryCache.evictAll();
            }
        });
    }

    class ImageLoadingTask extends AsyncTask<Void, Void, Bitmap> {
        String imageUrl;
        ImageLoadingTask(String image) {
            imageUrl = image;
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream is = (InputStream) conn.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;

            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    class ImageLoadingTask2 extends AsyncTask<Void, Void, Bitmap> {
        String imageUrl;
        ImageLoadingTask2(String image) {
            imageUrl = image;
        }

        @Override
        protected void onPreExecute() {
            mImageView.setImageBitmap(null);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream is = (InputStream) conn.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;

            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
            // 이미지 캐시에 추가
            mMemoryCache.put(imageUrl, bitmap);
        }
    }
}
