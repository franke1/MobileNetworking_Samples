package com.vanillastepdev.example.socketio;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_ADDRESS = "http://192.168.0.45:3000";
    private static final String TAG = "SocketIO";
    private EditText userInput;
    private TextView resultView;
    private ChatService chatService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = (EditText) findViewById(R.id.messageInput);
        resultView = (TextView) findViewById(R.id.resultView);

        handler = new Handler();

        chatService = new ChatService();

        findViewById(R.id.connectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatService.connectToServer();
            }
        });

        findViewById(R.id.disconnectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatService.close();
            }
        });

        findViewById(R.id.chageNameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameChangeDialog();
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = userInput.getText().toString();
                chatService.sendMessage(message);
            }
        });
    }
    // 채팅 이름 변경 다이얼로그
    private AlertDialog chatnameChangeDialog;
    private void showNameChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이름 입력")
                .setView(R.layout.dialog_rename)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editText = (EditText) chatnameChangeDialog.findViewById(R.id.chatName);
                        String chatName = editText.getText().toString();
                        chatService.changeName(chatName);
                    }
                })
                .setNegativeButton("Cancel", null);
        chatnameChangeDialog = builder.create();
        chatnameChangeDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class ChatService {
        Socket socket = null;
        ChatService() {
            try {
                socket = IO.socket(SERVER_ADDRESS);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.e(TAG, "Error : " + e.getMessage());
                return;
            }
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                }
            });

            socket.on("chat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "chat message : " + args[0]);
                    try {
                        JSONObject data = new JSONObject(args[0].toString());
                        final String message = data.getString("message");
                        final String name = data.getString("name");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                resultView.append(name + ">> " + message + "\n");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {}

            });
        }

        public void connectToServer() {
            socket.connect();
        }

        public void close() {
            socket.close();
        }

        public void changeName(String chatName) {
            String data = "{ \"name\" : \"" + chatName + "\"}";
            try {
                socket.emit("rename", new JSONObject(data));
            } catch (JSONException e) {
                Log.d(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            String data = "{ \"message\" : \"" + message + "\"}";
            Log.d(TAG, "Send : " + data);
            try {
                socket.emit("chatInput", new JSONObject(data));
            } catch (JSONException e) {
                Log.e(TAG, "Error : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
