package com.wannabewize.example.jsonrequest_volley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "JsonRequest";
    private TextView resultTextView;
    private RequestQueue queue;
    private String serverAddress = "http://192.168.0.129:3000/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText mNameField = (EditText) findViewById(R.id.nameEditText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        queue = Volley.newRequestQueue(this);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameField.getText().toString();
                sendRequest(name);
            }
        });
    }

    private void sendRequest(String name) {
        try {
            String jsonStr = "{ \"data\" : { \"name\":\"" + name + "\" } }";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, serverAddress, jsonStr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String result = null;
                    try {
                        result = response.toString(4);
                    } catch (JSONException e) {
                        result = "Error " + e.getMessage();
                    }
                    resultTextView.setText(result);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error : " + error.getMessage());
                    error.printStackTrace();
                }
            });
            queue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error " + e.getMessage());
            e.printStackTrace();
        }

    }
}
