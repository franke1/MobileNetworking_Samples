package com.example.wannabewize.netservice_android;

import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ServiceActivity extends AppCompatActivity {
    private static final String TAG = "NetService-Sample";

    private NsdServiceInfo mRemoteService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRemoteService = getIntent().getParcelableExtra(MainActivity.SERVICE_INTENT_NAME);
        if ( mRemoteService != null ) {
            Log.d(TAG, "trying to connect to service");
        }
        else {
            Log.d(TAG, "trying to make server");
        }
    }
}
