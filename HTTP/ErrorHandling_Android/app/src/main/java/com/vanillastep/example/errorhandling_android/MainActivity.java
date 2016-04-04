package com.vanillastep.example.errorhandling_android;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

   public static final String urlStr = "http://192.168.0.129:3000";
   private static final String TAG = "HandlingError-Sample";
   private TextView mResultView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mResultView = (TextView) findViewById(R.id.resultTextView);

      findViewById(R.id.badResponseButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            appendResult("= 요청 에러 =");
            new NetworkTask().execute(urlStr + "/badreq");
         }
      });

      findViewById(R.id.delayedResponseButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            appendResult("= 지연 응답 =");
            new NetworkTask().execute(urlStr + "/delayed");
         }
      });

      findViewById(R.id.infiniteResponseButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            appendResult("= 응답이 끝나지 않는 요청 =");
            new NetworkTask().execute(urlStr + "/infinite");
         }
      });
   }

   void appendResult(String msg) {
      mResultView.setText(mResultView.getText() + "\n" + msg);
   }

   class NetworkTask extends AsyncTask<String, String, String> {
      @Override
      protected String doInBackground(String... strings) {
         try {
            URL url = new URL(strings[0]);
            publishProgress("Trying to connect to " + url);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(7 * 1000); // 7초
            conn.setReadTimeout(7 * 1000); // 7초
            InputStream is = (InputStream) conn.getContent();
            Log.d(TAG, "InputStream : " + is);
            if ( is == null ) {
               return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer bufferStr = new StringBuffer();
            while( (line = reader.readLine()) != null ) {
               bufferStr.append(line);
            }

            final int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
               publishProgress("요청 성공 - StatusCode : " + code);
            } else {
               publishProgress("요청 실패. StatusCode : " + code);
            }

            return bufferStr.toString();

         } catch (SocketTimeoutException e) {
            publishProgress("Timeout Exception");
            Log.e(TAG, "Socket Timeout Exception", e);

         } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            e.printStackTrace();
         }

         return null;
      }

      @Override
      protected void onPostExecute(String s) {
         appendResult("응답 메세지 끝 : " + s);
      }

      @Override
      protected void onProgressUpdate(String... values) {
         appendResult(values[0]);
      }
   }
}
