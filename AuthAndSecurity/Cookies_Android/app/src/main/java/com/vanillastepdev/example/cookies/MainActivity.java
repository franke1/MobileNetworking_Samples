package com.vanillastepdev.example.cookies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_ADDRESS = "http://192.168.0.45:3000";
    private static final String TAG = "Cookies";
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = (TextView)findViewById(R.id.resultTextView);

        findViewById(R.id.httpUrlConnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.okHttpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new OkHttpCookiesTask().execute();
            }
        });

        findViewById(R.id.volleyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVolleyCookiesRequest();
            }
        });
    }

    private void sendVolleyCookiesRequest() {
        RequestQueue queue = RequestQueueSingleton.getInstance(this).getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(SERVER_ADDRESS, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String result = null;
                try {
                    result = response.toString(4);
                    resultView.setText(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    resultView.setText("Error : " + e.getMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultView.setText("Error : " + error.getMessage());
                Log.e(TAG, "Error ", error);
            }
        });
        queue.add(request);
    }

    private class OkHttpCookiesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Request request = new Request.Builder().url(SERVER_ADDRESS).get().build();
            OkHttpClient httpClient = OkHttpSingleton.sharedInstance().httpClient;
            try {
                Response response = httpClient.newCall(request).execute();
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
            }
            else {
                resultView.setText(s);
            }
        }
    }
}
