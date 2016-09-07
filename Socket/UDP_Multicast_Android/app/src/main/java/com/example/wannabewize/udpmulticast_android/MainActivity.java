package com.example.wannabewize.udpmulticast_android;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UdpMulticast";
    private static final String MULTICAST_ADDRESS = "239.255.255.255";
    private static final int MULTICAST_PORT = 49001;

    private MessageThread messageThread;
    private Handler mHandler;
    private TextView mMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageView = (TextView) findViewById(R.id.messageView);
        mHandler = new Handler();

        final EditText userInput = (EditText) findViewById(R.id.userInput);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = userInput.getText().toString();
                messageThread.sendMessage(message);

            }
        });

        messageThread = new MessageThread();
        messageThread.start();
    }

    // 메세지 수신/발신 용 쓰레드
    class MessageThread extends Thread {
        private MulticastSocket socket;

        public MessageThread() {
            try {
                socket = new MulticastSocket(MULTICAST_PORT);
                socket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));
            } catch (IOException e) {
                Log.e(TAG, "Error " + e.getMessage());
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
                        socket.send(packet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 버퍼
                    byte[] buffer = new byte[65507];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Block-API

                    // 데이터그램 패킷에서
                    final String received = new String(packet.getData()).trim();
                    final String sender = packet.getAddress().getHostName();
                    Log.d(TAG, "Received : " + received + " from " + sender);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mMessageView.append("\n" + sender + " >> " + received);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }
    }
}
