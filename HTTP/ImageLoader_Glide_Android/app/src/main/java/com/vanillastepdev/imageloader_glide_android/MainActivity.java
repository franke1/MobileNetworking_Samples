package com.vanillastepdev.imageloader_glide_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GlideExample";
    private ImageView mImageView;
    private String image = "http://www.ibiblio.org/wm/paint/auth/munch/munch.scream.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView)findViewById(R.id.imageView);

        findViewById(R.id.loadImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(null);
                Glide.with(MainActivity.this).load(image)
//                        .crossFade(3)
//                        .sizeMultiplier((float)0.5)
                        .into(mImageView);
            }
        });

        findViewById(R.id.loadImageButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageLoadingTask(image).execute();
            }
        });

        findViewById(R.id.clearCacheButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 로더 제거
                Glide.clear(mImageView);

                final Glide glide = Glide.get(MainActivity.this);
                glide.clearMemory();

                // 디스크 캐쉬 비우기는 멀티 쓰레드에서
                (new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Delete DiskCache");

                        glide.clearDiskCache();
                    }
                }).start();
            }
        });
    }

    class ImageLoadingTask extends AsyncTask<Void, Void, Bitmap> {
        String imageUrl;
        ImageLoadingTask(String image) {
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
        }
    }
}
