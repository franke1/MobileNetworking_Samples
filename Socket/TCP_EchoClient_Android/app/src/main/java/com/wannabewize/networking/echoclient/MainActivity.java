package com.wannabewize.networking.echoclient;

import android.os.Bundle;
import android.os.Handler;
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

   private static final String TAG = "EchoClientApp";

   static final String host = "192.168.0.129";
   static final int port = 3000;

   // 메세지 입력용
   private EditText userInput;
   // 결과 출력용
   private TextView resultView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      userInput = (EditText)findViewById(R.id.messageInput);
      resultView = (TextView)findViewById(R.id.resultView);

      findViewById(R.id.connectButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            connectToServer();
         }
      });

      findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            closeConnection();
         }
      });

      findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sendMessage();
            userInput.setText(null);
         }
      });
   }


   // 연결 버튼을 누르면 동작
   private void connectToServer() {

      try {
         resultView.append("서버 연결 시도\n");
         messageSender = new MessageThread();
         messageSender.start();
      } catch (Exception e) {
         resultView.append("연결 에러 : " + e.getMessage() + "\n");
         e.printStackTrace();
      }
   }


   // 종료 버튼을 누르면 동작
   private void closeConnection() {
      if ( messageSender != null && messageSender.isConnected() ) {
         messageSender.disconnect();
      }
      messageSender = null;
   }

   // 전송 버튼을 누르면 동작
   private void sendMessage() {
      if ( ! messageSender.isConnected() ) {
         Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
         return;
      }
      // 사용자가 입력한 문자열을 전송 쓰레드에게 보내기
      messageSender.sendMessage(userInput.getText().toString());
   }


   // UI에 컨텐츠 출력을 위한 핸들러
   private Handler handler = new Handler();

   private MessageThread messageSender;
   class MessageThread extends Thread {
      // 소켓
      private Socket socket;

      boolean isConnected() {
         return socket != null && socket.isConnected();
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

      public void disconnect() {
         try {
            socket.close();
            resultView.append("서버 연결 종료\n");
         } catch (IOException e) {
            resultView.append("연결 종료 에러 : " + e.getMessage() + "\n");
            Log.d(TAG, "Socket Close Error", e);
            e.printStackTrace();
         }
      }

      @Override
      public void run() {
         try {
            socket = new Socket(host, port);
            if ( false == socket.isConnected() ) {
               handler.post(new Runnable() {
                  @Override
                  public void run() {
                     resultView.append("서버 연결 실패\n");
                  }
               });
               return;
            }
            else {
               handler.post(new Runnable() {
                  @Override
                  public void run() {
                     resultView.append("서버 연결 성공\n");
                  }
               });
            }

            // 입력 스트림과 버퍼 기반의 Reader
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while ( true ) {
               final String line = reader.readLine();
               if ( line == null ) {
                  handler.post(new Runnable() {
                     @Override
                     public void run() {
                        resultView.append("서버와 연결 끊어짐\n");
                     }
                  });
                  break;
               }

               handler.post(new Runnable() {
                  @Override
                  public void run() {
                     resultView.append("서버 메세지 : " + line + "\n");
                  }
               });

               Log.d(TAG, "reading.. " + line);
            }

            Log.d(TAG, "quit..");

            reader.close();
            is.close();
            socket.close();
         } catch (IOException e) {
            Log.e(TAG, "Error : " + e.getMessage());
            e.printStackTrace();
         }
      } // run
   } // MessageThread - Thread
}
