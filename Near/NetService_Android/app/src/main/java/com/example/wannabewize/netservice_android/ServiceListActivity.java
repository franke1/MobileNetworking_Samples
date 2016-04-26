package com.example.wannabewize.netservice_android;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ServiceListActivity extends AppCompatActivity {

    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String TAG = "NetService-Sample";

    private List<NsdServiceInfo> serviceList = new ArrayList();

    private NsdManager mNsdManager;
    private Handler mHandler;
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return serviceList.size();
        }

        @Override
        public Object getItem(int i) {
            return serviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if ( view == null ) {
                LayoutInflater inflator = (LayoutInflater) ServiceListActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflator.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
            }

            TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
            TextView textView2 = (TextView) view.findViewById(android.R.id.text2);

            NsdServiceInfo info = (NsdServiceInfo) getItem(i);

            textView1.setText(info.getServiceName());

            InetAddress host = info.getHost();
            if ( host != null ) {
                textView2.setText(host.getHostAddress());
            }
            else {
                textView2.setText("address is not available");
            }

            return view;
        }
    };

    private NsdManager.ResolveListener mServiceResolveListner = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
            Log.d(TAG, "Resolve Failed ");
        }

        @Override
        public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
            int index = serviceList.indexOf(nsdServiceInfo);
            Log.d(TAG, "Service Resolved " + nsdServiceInfo.getHost() + " index : " + index);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {
        @Override
        public void onStartDiscoveryFailed(String s, int i) {
            isServiceDiscovering = false;
            Log.d(TAG, "onStartDiscoveryFailed");
        }

        @Override
        public void onStopDiscoveryFailed(String s, int i) {
            isServiceDiscovering = false;
            Log.d(TAG, "onStopDiscoveryFailed");
        }

        @Override
        public void onDiscoveryStarted(String s) {
            Log.d(TAG, "onDiscoveryStarted");
            serviceList.removeAll(serviceList);
            Toast.makeText(ServiceListActivity.this, "Discovery Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryStopped(String s) {
            Log.d(TAG, "onDiscoveryStopped");
            isServiceDiscovering = false;
            Toast.makeText(ServiceListActivity.this, "Discovery Stopped", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
            Log.d(TAG, "onServiceFound");
            if ( serviceList.indexOf(nsdServiceInfo) == -1 ) {
                serviceList.add(nsdServiceInfo);
            }
            if ( nsdServiceInfo.getHost() == null ) {
                mNsdManager.resolveService(nsdServiceInfo, mServiceResolveListner);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
            Log.d(TAG, "onServiceLost");
            serviceList.remove(nsdServiceInfo);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    boolean isServiceDiscovering = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        mNsdManager = (NsdManager)this.getSystemService(Context.NSD_SERVICE);
        mHandler = new Handler();

        ListView serviceListView = (ListView) findViewById(R.id.serviceListView);
        serviceListView.setAdapter(mAdapter);

        findViewById(R.id.newServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 새 서비스 생성
            }
        });

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 검색 버튼
                if ( isServiceDiscovering == false ) {
                    isServiceDiscovering = true;
                    mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
                }
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 서비스 검색 중지
            }
        });
    }
}
