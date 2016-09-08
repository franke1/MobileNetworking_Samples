package com.vanillastepdev.example.cookies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.common.io.CharStreams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_ADDRESS = "http://192.168.0.129:3000";
    private static final String TAG = "Cookies";
    private TextView resultView;
    private RequestQueue requestQueue;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = (TextView)findViewById(R.id.resultTextView);

        findViewById(R.id.httpUrlConnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultView.setText(null);
                new HttpUrlConnectionTask().execute();
            }
        });

        findViewById(R.id.okHttpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultView.setText(null);
                new OkHttpCookiesTask().execute();
            }
        });

        findViewById(R.id.volleyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultView.setText(null);
                sendVolleyCookiesRequest();
            }
        });

        findViewById(R.id.setCookiesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultView.setText(null);
                // 쿠키 저장소 설정
                NetworkSingleton.getInstance(MainActivity.this).setupCookiesStore();
                // OkHttp는 쿠키 설정된 HttpClient를 새로 생성
                httpClient = NetworkSingleton.getInstance(MainActivity.this).httpClient;
            }
        });

        requestQueue = NetworkSingleton.getInstance(this).requestQueue;
        httpClient = NetworkSingleton.getInstance(this).httpClient;
    }

    private void sendVolleyCookiesRequest() {
        JsonObjectRequest request = new JsonObjectRequest(SERVER_ADDRESS, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showResult(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultView.setText("Error : " + error.getMessage());
                Log.e(TAG, "Error ", error);
            }
        });
        requestQueue.add(request);
    }

    private class OkHttpCookiesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Request request = new Request.Builder().url(SERVER_ADDRESS).get().build();
            try {
                Response response = MainActivity.this.httpClient.newCall(request).execute();
                String result = response.body().string();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if ( s == null ) {
                resultView.setText("Error");
                return;
            }

            showResult(s);
        }
    }

    class HttpUrlConnectionTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(SERVER_ADDRESS);
                InputStream is = url.openStream();
                Log.d(TAG, "HttpUrlConnection : " + is.toString());
                String result = CharStreams.toString(new InputStreamReader(is));
                return result;
            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if ( s == null ) {
                resultView.setText("Error");
                return;
            }

            showResult(s);
        }
    }

    void showResult(String s) {
        try {
            showResult(new JSONObject(s));
        } catch (JSONException e) {
            e.printStackTrace();
            resultView.setText("JSON Error");
        }
    }

    void showResult(JSONObject root) {
        try {
            int visit = root.getInt("visit");
            String since = root.getString("since");
            String last = root.getString("last");
            resultView.setText("Visit : " + visit + "\nsince : " + since + "\nlast : " + last);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
