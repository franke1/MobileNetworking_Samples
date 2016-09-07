package com.vanillastep.example.websocket_android;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "WebSocketExample";
   String serverAddress = "ws://192.168.0.129:3000";
   private EditText mUserInput;
   private WebSocket mWebSocket;
   private TextView mChatMessages;
   Handler handler = new Handler();


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      findViewById(R.id.connectButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            connectToServer();
         }
      });

      mUserInput = (EditText)findViewById(R.id.userInput);
      findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            sendMessage();
         }
      });

      mChatMessages = (TextView)findViewById(R.id.chatMessages);
   }

   @Override
   protected void onResume() {
      super.onResume();
   }

   void sendMessage() {
      String message = mUserInput.getText().toString();
      Log.d(TAG, "Trying to send : " + message);
      if ( mWebSocket != null && mWebSocket.isOpen() ) {
         mWebSocket.send(message);
         mUserInput.setText("");
      }
   }


   void connectToServer() {
      Log.d(TAG, "trying to connect to websocket server");
      AsyncHttpClient client = AsyncHttpClient.getDefaultInstance();
      client.websocket(serverAddress, null, new AsyncHttpClient.WebSocketConnectCallback() {
         @Override
         public void onCompleted(Exception ex, WebSocket webSocket) {
            if ( ex != null ) {
               ex.printStackTrace();
               return;
            }

            mWebSocket = webSocket;

            webSocket.setStringCallback(new WebSocket.StringCallback() {
               @Override
               public void onStringAvailable(String s) {
                  Log.d(TAG, "String Callback : " + s);
                  final String message = s;
                  handler.post(new Runnable() {
                     @Override
                     public void run() {
                        mChatMessages.setText(mChatMessages.getText() + "\n" + message);
                     }
                  });
               }
            });

            webSocket.setDataCallback(new DataCallback() {
               @Override
               public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                  Log.d(TAG, "emitter : " + bb.peekString());
               }
            });

            Log.d(TAG, "on Completed");
         }
      });
   }
}
