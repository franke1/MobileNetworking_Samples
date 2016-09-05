package com.vanillastepdev.imageloader_glide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GlideExample";
    private ImageView mImageView;
    private String image = "http://www.ibiblio.org/wm/paint/auth/munch/munch.scream.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);

        findViewById(R.id.loadImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 로더 제거
                //Glide.clear(mImageView);
                mImageView.setImageBitmap(null);

                Glide.with(MainActivity.this).load(image)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL) // 모든 이미지 캐시
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 캐시 안함
                        .crossFade(1) // Animation
//                        .sizeMultiplier((float)0.5)
                        .into(mImageView);
            }
        });


        findViewById(R.id.clearCacheButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 로더 제거
                Glide.clear(mImageView);

                final Glide glide = Glide.get(MainActivity.this);
                // 메모리 캐시 비우기
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
}
