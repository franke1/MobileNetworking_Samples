package com.example.wannabewize.netservice_android;

import android.content.DialogInterface;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceActivity extends AppCompatActivity {
    private static final String TAG = "NetService-Sample";
    private ChatThread mChatThread;
    private TextView mMessageView;
    private Handler mHandler;
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {
        @Override
        public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            showMessage("서비스 등록 실패");
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            showMessage("서비스 등록 해제 실패");
        }

        @Override
        public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
            showMessage("서비스 등록 성공");
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
            showMessage("서비스 해제 성공");
        }
    };
    private AlertDialog mNewServiceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        mMessageView = (TextView)findViewById(R.id.messageView);
        mHandler = new Handler();

        findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceActivity.this.finish();
            }
        });

        final EditText editText = (EditText) findViewById(R.id.editText);
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                if ( mChatThread != null ) {
                    showMessage("me >> "+message);
                    mChatThread.sendMessage(message);
                }
            }
        });

    }

    void showMessage(final String message) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            mMessageView.setText(mMessageView.getText() + "\n" + message);
        else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMessageView.setText(mMessageView.getText() + "\n" + message);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NsdServiceInfo serviceInfo = getIntent().getParcelableExtra(MainActivity.SERVICE_INTENT_NAME);
        if ( serviceInfo != null ) {
            Log.d(TAG, "trying to connect to service");
            showMessage("서비스에 연결 중");
            connectToService(serviceInfo);
        }
        else {
            Log.d(TAG, "trying to make server");
            showMessage("새로운 서비스 생성");
            makeNewService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if ( mChatThread != null ) {
            mChatThread.closeChat();
        }
    }


    private void makeNewService() {
        // 서비스 이름 설정 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("서비스 이름 입력")
            .setView(R.layout.dialog_new_service)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText editText = (EditText) mNewServiceDialog.findViewById(R.id.serviceName);
                    String serviceName = editText.getText().toString();
                    startService(serviceName);
                }
            })
            .setNegativeButton("Cancel", null);
        mNewServiceDialog = builder.create();
        mNewServiceDialog.show();
    }

    private void startService(String serviceName) {
        // 서비스 생성
        NsdServiceInfo service = new NsdServiceInfo();
        service.setServiceName(serviceName);
        service.setServiceType(MainActivity.SERVICE_TYPE);
        service.setPort(3000);

        mNsdManager = (NsdManager) this.getSystemService(NSD_SERVICE);
        mNsdManager.registerService(service, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        ServerThread mServerThread = new ServerThread();
        mServerThread.start();
    }

    private void connectToService(NsdServiceInfo service) {
        mChatThread = new ChatThread(service);
        mChatThread.start();
    }

    // 서비스 서버 쓰레드
    class ServerThread extends Thread {
        private ServerSocket mServerSocket;

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(3000);
                Log.d(TAG, "ServerSocket Created, awaiting connection");
                showMessage("서비스 서버 준비 완료");

                Socket socket = mServerSocket.accept();

                showMessage("서비스 클라이언트 접속");

                Log.d(TAG, "Socket accepted, socket : " + socket);

                mChatThread = new ChatThread(socket);
                mChatThread.start();

                // 1:1 연결만 가능
                mServerSocket.close();
            }
            catch ( Exception e ) {
                Log.e(TAG, "Server Thread Exception : " + e.getMessage());
                showMessage("서비스 서버 동작 실패 : " + e.getMessage());
            }
        }
    }

    // 채팅 쓰레드
    class ChatThread extends Thread {
        private Socket mSocket;
        private NsdServiceInfo mService = null;
        private BufferedReader reader = null;
        private BufferedWriter writer = null;

        ChatThread(Socket socket) {
            mSocket = socket;
        }

        ChatThread(NsdServiceInfo service) {
            mService = service;
        }

        public void sendMessage(String msg) {
            try {
                writer.write(msg);
                writer.flush();
            } catch (IOException e) {
                Log.e(TAG, "Write Message Error : " + e.getMessage());
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "Chatting Thread is running");
            try {
                prepareChatThread();
                
                String line = null;

                while ( (line = reader.readLine()) != null ) {
                    Log.d(TAG, "Got Message : " + line);
                    showMessage("Other >> " + line);
                }
                Log.d(TAG, "Chat Thread end");
                showMessage("채팅 종료");
            }
            catch ( Exception e ) {
                Log.e(TAG, "Chatter Running Exception : " + e.getMessage());
            }
        }

        private void prepareChatThread() {
            try {
                if ( mSocket == null && mService != null ) {
                    InetAddress address = mService.getHost();
                    int port = mService.getPort();
                    Log.d(TAG, "Trying to Service : " + address + " @ " + port);
                    mSocket = new Socket(address, port);
                }

                InputStream is = mSocket.getInputStream();
                OutputStream os = mSocket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                writer = new BufferedWriter(new OutputStreamWriter(os));
                showMessage("서비스 연결 성공");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void closeChat() {
            try {
                if ( reader != null )
                    reader.close();
                if ( writer != null )
                    writer.close();
                mSocket.close();
            }
            catch ( Exception e ) {
                Log.e(TAG, "Close Chatter : " + e.getMessage());
            }
        }
    }
}