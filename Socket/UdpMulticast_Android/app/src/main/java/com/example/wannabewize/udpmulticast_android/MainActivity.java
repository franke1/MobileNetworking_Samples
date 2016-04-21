package com.example.wannabewize.udpmulticast_android;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.http.AsyncSocketMiddleware;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UdpMulticast";
    private static final String MULTICAST_ADDRESS = "224.0.0.114";
    private static final int MULTICAST_PORT = 3000;
    private MulticastSocket mSocket;
    private MessageReceiveThread mReceiveThread;
    private Handler mHandler;
    private TextView mMessageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText userInput = (EditText) findViewById(R.id.userInput);
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = userInput.getText().toString();
                sendMessage(message);

            }
        });

        mHandler = new Handler();
        mMessageView = (TextView) findViewById(R.id.messageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readyUdpSocket();
    }

    void readyUdpSocket() {
        try {
            mSocket = new MulticastSocket(MULTICAST_PORT);
            mSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));

            mReceiveThread = new MessageReceiveThread();
            mReceiveThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Socket Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    void sendMessage(final String message) {
        new Thread() {
            @Override
            public void run() {
                byte[] data = message.getBytes();
                DatagramPacket packet = null;
                try {
                    packet = new DatagramPacket(data, data.length, InetAddress.getByName(MULTICAST_ADDRESS), MULTICAST_PORT);
                    mSocket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 메세지 수신 용 쓰레드
    class MessageReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    // 버퍼
                    byte[] buffer = new byte[65507];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    // 블록 코드다.
                    mSocket.receive(packet);

                    // 데이터그램 패킷에서
                    final String received = new String(packet.getData()).trim();
                    Log.d(TAG, "Received : " + received);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mMessageView.append(received + "\n");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }
    }
}
