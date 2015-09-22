package com.vanillastepdev.example.echoudp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

   static final String host = "192.168.0.16";
   static final int port = 3001;

   private static final String TAG = "UDP_Exercise";

   private EditText mMessage;
   private TextView mResultView;
   private Handler mHandler;

   private MessageReceiver mMessageThread;
   private DatagramSocket mSocket;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // 서버 주소 출력
      TextView address = (TextView)findViewById(R.id.address);
      address.setText("UDP Echo Server - " + host + ":" + port);

      mMessage = (EditText) findViewById(R.id.message);
      mResultView = (TextView) findViewById(R.id.resultView);

      // 화면 출력용
      mHandler = new Handler();

      try {
         mSocket = new DatagramSocket();
      } catch (SocketException e) {
         e.printStackTrace();
      }

      mMessageThread = new MessageReceiver();
      mMessageThread.start();
   }

   public void sendMessage(View v) {
      final String msg = mMessage.getText().toString();
      mResultView.append("Send >> " + msg + "\n");
      // 메세지 발송 용 쓰레드 생성
      new Thread() {
         @Override
         public void run() {
            try {
               byte[] data = msg.getBytes();
               DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
               mSocket.send(packet);
            } catch (Exception e) {
               e.printStackTrace();
               Log.e(TAG, "Exception", e);
            }
         }
      }.start();
   }

   // 메세지 수신 용 쓰레드
   class MessageReceiver extends Thread {
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
                     mResultView.append("Receive >> " + received + "\n");
                  }
               });
            }
         } catch (Exception e) {
            Log.e(TAG, "Error", e);
         }
      }
   }
}
