package com.example.wannabewize.netservice_android;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String SERVICE_TYPE = "_http._tcp.";
    private static final String TAG = "NetService-Sample";
    public static final String SERVICE_INTENT_NAME = "SERVICE_INFO";

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
                LayoutInflater inflator = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            Toast.makeText(MainActivity.this, "Discovery Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryStopped(String s) {
            Log.d(TAG, "onDiscoveryStopped");
            isServiceDiscovering = false;
            Toast.makeText(MainActivity.this, "Discovery Stopped", Toast.LENGTH_SHORT).show();
        }

        NsdServiceInfo getServiceInfoWithName(String name) {
            for (NsdServiceInfo service : serviceList) {
                if ( service.getServiceName().equals(name)) {
                    return service;
                }
            }
            return null;
        }

        @Override
        public void onServiceFound(final NsdServiceInfo nsdServiceInfo) {
            Log.d(TAG, "onServiceFound");

            NsdServiceInfo service = getServiceInfoWithName(nsdServiceInfo.getServiceName());
            if ( service == null ) {
                serviceList.add(nsdServiceInfo);
            }

            // 주소 정보 얻어오기
            if ( nsdServiceInfo.getHost() == null ) {
                mNsdManager.resolveService(nsdServiceInfo, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo info, int i) {
                        Log.d(TAG, info.getServiceName() + " resolve failed");
                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo info) {
                        Log.d(TAG, info.getServiceName() + " resolve successful " + info.getHost());
                        // 얻어온 주소를 서비스 정보에 설정
                        nsdServiceInfo.setHost(info.getHost());
                        nsdServiceInfo.setPort(info.getPort());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
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
            NsdServiceInfo service = getServiceInfoWithName(nsdServiceInfo.getServiceName());
            Log.d(TAG, "onServiceLost : " + nsdServiceInfo.getServiceName());
            if ( service != null ) {
                serviceList.remove(nsdServiceInfo);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    boolean isServiceDiscovering = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNsdManager = (NsdManager)this.getSystemService(Context.NSD_SERVICE);
        mHandler = new Handler();

        ListView serviceListView = (ListView) findViewById(R.id.serviceListView);
        serviceListView.setAdapter(mAdapter);
        serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NsdServiceInfo service = (NsdServiceInfo) adapterView.getAdapter().getItem(i);
                Log.d(TAG, "Trying to Connect to Service : " + service.getServiceName());
                Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                intent.putExtra(SERVICE_INTENT_NAME, service);
                MainActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.newServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 새 서비스 생성
                Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                MainActivity.this.startActivity(intent);
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
                if ( isServiceDiscovering ) {
                    mNsdManager.stopServiceDiscovery(discoveryListener);
                }
            }
        });
    }
}
