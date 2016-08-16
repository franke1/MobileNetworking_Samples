package com.vanillastepdev.example.cloudmessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CloudMessage-Example";
    private static final String SERVER_ADDRESS = "http://192.168.10.161:3000";
    private static final String REGIST_DEVICE_URL = SERVER_ADDRESS + "/regist";

    private TextView registrationToken;
    private TextView deviceIdLabel;
    private OkHttpClient client;
    private String androidID;
    private String token;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 토큰 레이블
        registrationToken = (TextView) findViewById(R.id.registrationToken);
        // 저장된 토큰이 있으면 출력
        restoreToken();
        // 기기 ID 레이블
        deviceIdLabel = (TextView)findViewById(R.id.deviceIdLabel);

        client = new OkHttpClient.Builder().build();

        handler = new android.os.Handler();

        // 토큰 발급받기
        findViewById(R.id.requestTokenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "Registration Token : " + token);
                registrationToken.setText(token);
            }
        });

        // 토큰 저장
        findViewById(R.id.saveTokenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToken();
            }
        });

        findViewById(R.id.getDeviceIDButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceId();
            }
        });

        // 서버에 토큰 등록하기
        findViewById(R.id.registTokenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (token != null && androidID != null) {
                    new TokenRegistThread().start();
                } else {
                    Toast.makeText(MainActivity.this, "토큰 AndroidID 발급 필요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class TokenRegistThread extends Thread {
        @Override
        public void run() {
            RequestBody body = new FormBody.Builder().add("token", token).add("deviceId", androidID).build();
            Request request = new Request.Builder().url(REGIST_DEVICE_URL).method("POST", body).build();
            try {
                final Response response = client.newCall(request).execute();
                String bodyStr = response.body().string();
                Log.d(TAG, "body : " + bodyStr);

                JSONObject bodyNode = new JSONObject(bodyStr);
                final String responseMsg = bodyNode.getString("msg");

                if ( response.isSuccessful() ) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "기기 등록 성공", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, responseMsg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error : " + e.getMessage());
            }
        }
    }

    // 기기 ID
    private void getDeviceId() {
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceIdLabel.setText(androidID);
    }

    private void restoreToken() {
        // Preference 얻기
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        token = preferences.getString("REGISTRATION_ID", null);
        Log.d(TAG, "Restore Token : " + token);
        if ( token != null ) {
            registrationToken.setText(token);
        }
    }

    private void saveToken() {
        String token = registrationToken.getText().toString();
        if ( token == null ) {
            Toast.makeText(this, "Token is not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preference 얻기
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        // Preference 저장
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("REGISTRATION_ID", token);
        // 저장
        Boolean ret = editor.commit();

        if ( ret ) {
            Toast.makeText(this, "토큰 저장 성공", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "토큰 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
