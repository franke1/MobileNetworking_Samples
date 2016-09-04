package com.wannabewize.example.apachehttplibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ApacheLibExample";

    private DefaultHttpClient httpClient;
    private ImageView imageView;
    private TextView textView;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = new DefaultHttpClient();

        imageView = (ImageView)findViewById(R.id.imageView);
        textView = (TextView)findViewById(R.id.textView);

        findViewById(R.id.showImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                new NetworkThread().start();
//                new NetworkTask().execute();
            }
        });
    }

    class NetworkTask extends AsyncTask<Void, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Void... params) {
            try {
                // Random Placeholder Image
                String urlStr = "http://lorempixel.com/720/1080/cats/";

                HttpGet req = new HttpGet(urlStr);

                HttpResponse res = httpClient.execute(req);
                int statusCode = res.getStatusLine().getStatusCode();
                Log.d(TAG, "Status Code : " + statusCode);

                final String responseStr = res.getStatusLine().toString();

                InputStream is = res.getEntity().getContent();
                final Bitmap bitmap = BitmapFactory.decodeStream(is);

                Bundle bundle = new Bundle();
                bundle.putString("RESPONSE", responseStr);
                bundle.putParcelable("IMAGE", bitmap);

                return bundle;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            if ( bundle != null ) {
                imageView.setImageBitmap((Bitmap) bundle.getParcelable("IMAGE"));
                textView.setText(bundle.getString("RESPONSE"));
            }
        }
    }

    class NetworkThread extends Thread {
        @Override
        public void run() {
            try {
                // Random Placeholder Image
                String urlStr = "http://lorempixel.com/720/1080/cats/";

                HttpGet req = new HttpGet(urlStr);

                HttpResponse res = httpClient.execute(req);
                int statusCode = res.getStatusLine().getStatusCode();
                Log.d(TAG, "Status Code : " + statusCode);

                final String responseStr = res.getStatusLine().toString();

                InputStream is = res.getEntity().getContent();
                final Bitmap bitmap = BitmapFactory.decodeStream(is);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(responseStr);
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
