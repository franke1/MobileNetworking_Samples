package com.vanillastep.example.bluetoothchat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

   private static final String TAG = "DeviceListActivity";
   private static final int COARSE_LOCATION_PERMISSION_REQUEST = 1;
   public static String SELECTED_DEVICE_ADDRESS = "device_address";


   private ArrayAdapter<String> pairedDeviceAdapter;
   private ArrayAdapter<String> newDeviceAdapter;

   private BluetoothAdapter bluetoothAdapter;
   private ArrayList<BluetoothDevice> deviceList;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_device_list);

      bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

      ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
      pairedDeviceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
      pairedListView.setAdapter(pairedDeviceAdapter);
      pairedListView.setOnItemClickListener(mDeviceClickListener);


      ListView newDeviceListView = (ListView)findViewById(R.id.new_devices);
      newDeviceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
      newDeviceListView.setAdapter(newDeviceAdapter);
      newDeviceListView.setOnItemClickListener(mDeviceClickListener);

      IntentFilter filter = new IntentFilter();
      filter.addAction(BluetoothDevice.ACTION_FOUND);
      filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

      this.registerReceiver(mReceiver, filter);

      resolvePairedDevices();

      findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startDiscover();
         }
      });

      findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            stopDiscover();
         }
      });
   }

   @Override
   protected void onStop() {
      super.onStop();

      // 중지
      if (bluetoothAdapter != null) {
         bluetoothAdapter.cancelDiscovery();
      }
      this.unregisterReceiver(mReceiver);
   }

   @Override
   protected void onResume() {
      super.onResume();
      startDiscover();
   }

   private void stopDiscover() {
      setResult(Activity.RESULT_CANCELED);
      finish();
   }


   // 런타임 권한 요청 결과
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if ( COARSE_LOCATION_PERMISSION_REQUEST == requestCode ) {
         int requestResult = grantResults[0];
         if ( requestResult == PackageManager.PERMISSION_GRANTED ) {
            startDiscover();
         }
         else {
            Toast.makeText(this, "위치 접근 권한 승인이 필요합니다", Toast.LENGTH_SHORT).show();
         }
      }
   }

   void startDiscover() {
      // 블루투스 장치 검색을 위한 런타임 권한 체크
      int coarseLocationCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

      if ( coarseLocationCheck != PackageManager.PERMISSION_GRANTED ) {
         Log.d(TAG, "ACCESS_COARSE_LOCATION 권한 요청 필요 " + coarseLocationCheck);
         String[] permission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
         ActivityCompat.requestPermissions(this, permission, COARSE_LOCATION_PERMISSION_REQUEST);
         return;
      }

      newDeviceAdapter.clear();
      newDeviceAdapter.notifyDataSetChanged();

      // 검색 중이면 중지 후 기기 검색
      if (bluetoothAdapter.isDiscovering()) {
         bluetoothAdapter.cancelDiscovery();
      }
      bluetoothAdapter.startDiscovery();
   }

   private BroadcastReceiver mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         if ( BluetoothDevice.ACTION_FOUND.equals(action)) {
            String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Log.d(TAG, "New Device - name : " + name + " Address : " + device.getAddress());
            newDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
            newDeviceAdapter.notifyDataSetChanged();
         }
         else if ( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            // 블루투스 기기 검색 시작
            Log.d(TAG, "블루투스 기기 검색 시작");
         }
         else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            // 블루투스 기기 검색 끝
            Log.d(TAG, "블루투스 기기 검색 종료");
         }
      }
   };



   private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         bluetoothAdapter.cancelDiscovery();

         String info = (String) parent.getAdapter().getItem(position);//((TextView) view).getText().toString();
         String address = info.substring(info.length() - 17);

         Log.d(TAG, "Selected : " + info);

         Intent intent = new Intent();
         intent.putExtra(SELECTED_DEVICE_ADDRESS, address);

         setResult(Activity.RESULT_OK, intent);
         finish();
      }
   };

   private void resolvePairedDevices() {
      Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

      // If there are paired deviceList, add each one to the ArrayAdapter
      if (pairedDevices.size() > 0) {
         findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
         for (BluetoothDevice device : pairedDevices) {
            pairedDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
            Log.d(TAG, "Faired Device - name : " + device.getName() + " Address : " + device.getAddress());
         }
      }
      else {
         TextView tv = (TextView) findViewById(R.id.title_paired_devices);
         tv.setText("No Paired Device");
      }
   }

}
