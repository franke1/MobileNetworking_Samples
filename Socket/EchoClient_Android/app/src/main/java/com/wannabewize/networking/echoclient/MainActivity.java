package com.wannabewize.networking.echoclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

   private static final String TAG = "EchoClientApp";

   // 소켓
   private Socket socket;
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
   }


   // 연결 버튼을 누르면 동작
   public void connectToServer(View v) {
      EditText ipInput = (EditText) findViewById(R.id.ipInput);
      EditText portInput = (EditText) findViewById(R.id.portInput);

      try {
         String host = ipInput.getText().toString();
         int port = Integer.parseInt(portInput.getText().toString());

         messageSender = new MessageSender(host, port);
         messageSender.start();
      } catch (Exception e) {
         e.printStackTrace();
         String msg = resultView.getText() + "\n" + e.getMessage();
         resultView.setText(msg);
      }
   }


   // 종료 버튼을 누르면 동작
   public void closeConnection(View v) {
      messageSender.disconnect();
   }

   // 전송 버튼을 누르면 동작
   public void sendMessage(View v) {
      if ( ! messageSender.isConnected() ) {
         Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
         return;
      }
      // 사용자가 입력한 문자열을 전송 쓰레드에게 보내기
      messageSender.sendMessage(userInput.getText().toString());
   }


   // UI에 컨텐츠 출력을 위한 핸들러
   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         if ( -1 == msg.what ) {
            resultView.append("서버와 연결 실패\n");
         }
         else if ( 1 == msg.what ) {
            resultView.append("서버와 연결 성공\n");
         }
         else if ( 2 == msg.what ) {
            resultView.append("서버 메세지 : " + msg.obj + "\n");

         }
      }
   };


   private MessageSender messageSender;
   class MessageSender extends Thread {
      private String host;
      private int port;

      MessageSender(String host, int port) throws IOException {
         this.host = host;
         this.port = port;
      }

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
         } catch (IOException e) {
            Log.d(TAG, "Socket Close Error", e);
            e.printStackTrace();
         }
      }

      @Override
      public void run() {
         try {
            socket = new Socket(host, port);
            if ( false == socket.isConnected() ) {
               handler.sendEmptyMessage(-1);
               return;
            }
            else {
               handler.sendEmptyMessage(1);
            }

            // 입력 스트림과 버퍼 기반의 Reader
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;

            while ( true ) {
               line = reader.readLine();
               if ( line == null ) {
                  break;
               }

               Message msg = new Message();
               msg.what = 2;
               msg.obj = line;
               handler.sendMessage(msg);
               Log.d(TAG, "reading.. " + line);
            }

            Log.d(TAG, "quit..");

            reader.close();
            is.close();
            socket.close();
         } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
         }
      } // run
   } // MessageSender - Thread
}
