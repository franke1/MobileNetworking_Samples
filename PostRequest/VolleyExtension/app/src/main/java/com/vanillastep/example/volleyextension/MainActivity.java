package com.vanillastep.example.volleyextension;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.navercorp.volleyextensions.volleyer.factory.DefaultNetworkResponseParserFactory;
import com.navercorp.volleyextensions.volleyer.http.ContentTypes;
import com.navercorp.volleyextensions.volleyer.response.parser.IntegratedNetworkResponseParser;
import com.navercorp.volleyextensions.volleyer.response.parser.NetworkResponseParser;
import com.navercorp.volleyextensions.volleyer.response.parser.TypedNetworkResponseParser;

import static com.navercorp.volleyextensions.volleyer.Volleyer.*;


public class MainActivity extends AppCompatActivity {

   private static final String TAG = "VolleyExtension";
   private RequestQueue mQueue;
   private TextView mTextView;
   private ImageView mImageView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTextView = (TextView)findViewById(R.id.textView);
      mImageView = (ImageView)findViewById(R.id.imageView);

      findViewById(R.id.textRequestButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            mTextView.setText("");
            mImageView.setImageBitmap(null);

            sendTextRequest();
         }
      });

      findViewById(R.id.imageRequestButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            mTextView.setText("");
            mImageView.setImageBitmap(null);

            sendImageRequest();
         }
      });
      
      mQueue = Volley.newRequestQueue(this);
   }

   void sendImageRequest() {
      String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Meisje_met_de_parel.jpg/600px-Meisje_met_de_parel.jpg";
//      NetworkResponseParser parser = new IntegratedNetworkResponseParser().

      NetworkResponseParser parser = new IntegratedNetworkResponseParser.Builder().addParser("image/*", new NetworkResponseParser() {
         @Override
         public <Bitmap> Response<Bitmap> parseNetworkResponse(NetworkResponse response, Class<Bitmap> clazz) {
            Bitmap bitmap = (Bitmap) BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
            return (Response<Bitmap>) bitmap;
         }
      });

      volleyer(mQueue).get(url)
              .withTargetClass(Bitmap.class)
              .withResponseParser(parser)
              .withListener(new Response.Listener<Bitmap>() {
                 @Override
                 public void onResponse(Bitmap response) {
                    mImageView.setImageBitmap(response);
                 }
              })
              .withErrorListener(new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                    mTextView.setText(error.getStackTrace().toString());
                    Log.d(TAG, "Error : " + error.toString());

                 }
              })
              .execute();
   }

   void sendTextRequest() {
      volleyer(mQueue)
              .get("http://google.com")
              .withListener(new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                    mTextView.setText(response);
                 }
              })
              .withErrorListener(new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                 }
              })
              .execute();
   }
}
