package com.vanillastep.example.chatclient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

   final String host = "192.168.0.129";
   final int port = 3000;

   private static final String TAG = "ChatClient_Example";

   private EditText userInput;
   private TextView resultView;

   // 채팅 쓰레드
   ChatThread chatThread;

   // 채팅 이름 변경 다이얼로그
   private AlertDialog chatnameChangeDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      userInput = (EditText) findViewById(R.id.messageInput);
      resultView = (TextView) findViewById(R.id.resultView);

      findViewById(R.id.connectButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            connectToServer();
         }
      });

      findViewById(R.id.disconnectButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            closeConnection();
         }
      });

      findViewById(R.id.chageNameButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (chatThread != null && chatThread.isConnected() ) {
               showNameChangeDialog();
            }
            else {
               Toast.makeText(MainActivity.this, "서비스에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
         }
      });
   }

   private void showNameChangeDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("이름 입력")
              .setView(R.layout.chatname_dialog)
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                    EditText editText = (EditText) chatnameChangeDialog.findViewById(R.id.chatName);
                    String chatName = editText.getText().toString();
                    chatThread.changeChatName(chatName);
                 }
              })
              .setNegativeButton("Cancel", null);
      chatnameChangeDialog = builder.create();
      chatnameChangeDialog.show();
   }

   // 연결 버튼의 이벤트 리스너
   private void connectToServer() {
      if (null != chatThread && chatThread.isConnected()) {
         Toast.makeText(this, "이미 연결돼있습니다.", Toast.LENGTH_SHORT).show();
         return;
      }

      try {
         chatThread = new ChatThread();
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

   private Handler handler = new Handler();

   class ChatThread extends Thread {
      private Socket socket;

      boolean isConnected() {
         if (socket == null)
            return false;
         return socket.isConnected();
      }

      void sendMessage(String message) {
         try {
            // Log.d(TAG, "메인 쓰레드 ? " + (Looper.getMainLooper() == Looper.myLooper()));
            OutputStream os = socket.getOutputStream();
            os.write(message.getBytes());
            os.flush();
         } catch (IOException e) {
            Log.e(TAG, "Exception", e);
            e.printStackTrace();
         }
      }

      void changeChatName(String name) {
         try {
            OutputStream os = socket.getOutputStream();
            String renameCtrl = "\\rename " + name;
            os.write(renameCtrl.getBytes());
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
            while (true) {
               final String line = reader.readLine();
               if (line == null)
                  break;

               handler.post(new Runnable() {
                  @Override
                  public void run() {
                     resultView.append(line + "\n");
                  }
               });
            }
         } catch (IOException e) {
            Log.e(TAG, "Exception", e);
         }
         finally {
            Log.d(TAG, "closing socket, reader, is");
            handler.post(new Runnable() {
               @Override
               public void run() {
                  resultView.append("Chat service disconnected");
               }
            });

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
