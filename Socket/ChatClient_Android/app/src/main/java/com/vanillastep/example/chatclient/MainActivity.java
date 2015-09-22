package com.vanillastep.example.chatclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

   String host = "192.168.0.16";
   int port = 3000;

   private static final String TAG = "ChatClient_Example";

   private EditText userInput;
   private TextView resultView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      userInput = (EditText) findViewById(R.id.messageInput);
      resultView = (TextView) findViewById(R.id.resultView);

      Button connectButton = (Button)findViewById(R.id.connectButton);
      connectButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            connectToServer();
         }
      });

      Button disconnectButton = (Button)findViewById(R.id.disconnectButton);
      disconnectButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            closeConnection();
         }
      });

      TextView address = (TextView) findViewById(R.id.serverAddress);
      address.setText(host + ":" + port);
   }

   // 연결 버튼의 이벤트 리스너
   private void connectToServer() {
      if (null != chatThread && chatThread.isConnected()) {
         Toast.makeText(this, "이미 연결돼있습니다.", Toast.LENGTH_SHORT).show();
         return;
      }

      try {
         chatThread = new ChatThread(host, port);
         chatThread.start();
      } catch (Exception e) {
         Log.e(TAG, "Connect Error", e);
         e.printStackTrace();

         String msg = resultView.getText() + "\n" + e.getMessage();
         resultView.setText(msg);
      }
   }

   // 전송 버튼의 이벤트 리스너
   public void sendMessage(View v) {
      if (chatThread != null && chatThread.isConnected()) {
         // 사용자가 입력한 문자열을 전송 쓰레드를 통해서 보내기
         String message = userInput.getText().toString() + "\n";
         chatThread.sendMessage(message);
      } else {
         Toast.makeText(this, "서버와 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
      }
   }

   // 종료 버튼 이벤트 리스너
   private void closeConnection() {
      if (chatThread != null) {
         chatThread.closeChat();
         chatThread = null;
      }
   }

   // UI에 컨텐츠 출력을 위한 핸들러
   class ChatMessageHandler extends Handler {
      @Override
      public void handleMessage(Message msg) {
         String str = (String) msg.obj;
         resultView.setText(resultView.getText().toString() + "\n" + str);
      }

      public void printMessage(String str) {
         Message msg = new Message();
         msg.obj = str;
         sendMessage(msg);
      }
   }

   private ChatMessageHandler mHanlder = new ChatMessageHandler();

   // 채팅 쓰레드
   ChatThread chatThread;

   class ChatThread extends Thread {
      private String host;
      private int port;
      private Socket socket;

      ChatThread(String host, int port) {
         this.host = host;
         this.port = port;
      }

      boolean isConnected() {
         if (socket == null)
            return false;
         return socket.isConnected();
      }

      void sendMessage(String message) {
         try {
            OutputStream os = socket.getOutputStream();
            os.write(message.getBytes());
            os.flush();
         } catch (IOException e) {
            Log.e(TAG, "Exception", e);
            e.printStackTrace();
         }
      }

      void closeChat() {
         if (socket != null && socket.isConnected()) {
            try {
               socket.close();
            } catch (IOException e) {
               Log.e(TAG, "Error", e);
            }
         }
      }

      @Override
      public void run() {
         InputStream is = null;
         BufferedReader reader = null;
         try {
            socket = new Socket(host, port);
            Log.d(TAG, "Socket connected? " + socket.isConnected());

            is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while (true) {
               line = reader.readLine();
               if (line == null)
                  break;

               mHanlder.printMessage(line);
            }
         } catch (IOException e) {
            Log.e(TAG, "Exception", e);
         }
         finally {
            Log.d(TAG, "closing socket, reader, is");
            mHanlder.printMessage("Chat service disconnected");
            try {
               if ( reader != null )
                  reader.close();
               if ( is != null)
                  is.close();
               if ( socket != null )
                  socket.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

      }
   }
}
