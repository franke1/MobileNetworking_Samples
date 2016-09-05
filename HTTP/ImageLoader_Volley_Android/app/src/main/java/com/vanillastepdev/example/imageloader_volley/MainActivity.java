package com.vanillastepdev.example.imageloader_volley;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VolleyImageLoading";
    private NetworkImageView mImageView;
    private String image = "http://www.ibiblio.org/wm/paint/auth/munch/munch.scream.jpg";

    private ImageCache mImageCache;
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (NetworkImageView) findViewById(R.id.imageView);

        mQueue = Volley.newRequestQueue(this);
        mImageCache = new ImageCache(2 * 1000);
        mImageLoader = new ImageLoader(mQueue, mImageCache);

        findViewById(R.id.loadImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(null);
                Log.d(TAG, "ImageURL : " + mImageView.getImageURL());
                if ( mImageView.getImageURL() != null )
                    mImageView.setImageUrl(null, mImageLoader);
                mImageView.setImageUrl(image, mImageLoader);
            }
        });


        findViewById(R.id.clearCacheButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(null);

                // TODO:  잘 동작 안함
                // 캐시 비우기
                mImageCache.evictAll();
            }
        });
    }


    class ImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
        public ImageCache(int maxSize) {
            super(maxSize);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return this.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            this.put(url, bitmap);
        }
    }
}
