package com.example.wannabewize.nearchat_android;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NearChat-Example";
    private static final int FIND_CHATROOM_ACTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( FIND_CHATROOM_ACTION == requestCode ) {
            
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "OnOptionItemSelected");
        switch (item.getItemId()) {
            case R.id.findChatroomMenu:
                Log.d(TAG, "Find Chat Room");
                Intent intent = new Intent(this, FindChatroomActivity.class);
                startActivityForResult(intent, FIND_CHATROOM_ACTION);
                return true;
            case R.id.makeChatroomMenu:
                Log.d(TAG, "Make Char room");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
