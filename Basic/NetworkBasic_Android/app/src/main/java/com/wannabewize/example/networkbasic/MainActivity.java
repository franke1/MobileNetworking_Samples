package com.wannabewize.example.networkbasic;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NetworkBasic";
    private TextView mTextView;
    private Handler mHandler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.syncButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSyncAPI();
            }
        });

        findViewById(R.id.asyncCallButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new NetworkThread().start();
                NetworkTask task = new NetworkTask();
                new Thread(task).start();
            }
        });

        findViewById(R.id.asyncCallButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkThreadWithHandler().start();
            }
        });

        mHandler2 = new Handler();
        mTextView = (TextView)findViewById(R.id.contentTypeView);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == 1 ) {
                Object data = msg.obj;
                mTextView.setText((String)data);
            }
            else if ( msg.what == 2 ) {
                mTextView.setText("핸들러 다루기");
            }
        }
    };

    // 핸들러로 멀티 쓰레드 결과 다루기
    class NetworkThreadWithHandler extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL("http://google.com");
                URLConnection conn = url.openConnection();
                Log.d(TAG, "Content Type : " + conn.getContentType());
                final String contentType = conn.getContentType();

                // 1
//                Message msg = new Message();
//                msg.what = 1;
//                msg.obj = contentType;
//                mHandler.sendMessage(msg);

                // 2
//                mHandler.sendEmptyMessage(2);

                // 3
                mHandler2.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(contentType);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 멀티 쓰레드를 사용하는 네트워크
    class NetworkThread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL("http://google.com");
                URLConnection conn = url.openConnection();
                Log.d(TAG, "Content Type : " + conn.getContentType());
                String contentType = conn.getContentType();
                mTextView.setText(contentType);
            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 멀티 쓰레드를 사용하는 네트워크
    class NetworkTask extends Object implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL("http://google.com");
                URLConnection conn = url.openConnection();
                Log.d(TAG, "Content Type : " + conn.getContentType());
                String contentType = conn.getContentType();
                mTextView.setText(contentType);
            } catch (Exception e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    // 동기식 API 호출 - 에러 발생
    private void callSyncAPI() {
        try {
            URL url = new URL("http://google.com");
            URLConnection conn = url.openConnection();
            // 컨텐츠 타입 얻기
            Log.d(TAG, "Content Type : " + conn.getContentType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
