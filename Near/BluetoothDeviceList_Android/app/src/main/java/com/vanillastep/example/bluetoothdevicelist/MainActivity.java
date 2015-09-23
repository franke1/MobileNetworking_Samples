package com.vanillastep.example.bluetoothdevicelist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "Bluetooth-Sample";
   private static final int BLUETOOTH_ENABLE_REQUEST = 1;
   private BluetoothAdapter bluetoothAdapter;

   private TextView stateLabel;

   private ArrayAdapter<DeviceInfo> newDeviceListAdapter;
   private ArrayAdapter<DeviceInfo> pairedDeviceListAdapter;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      stateLabel = (TextView)findViewById(R.id.stateLabel);

      // 블루투스 아답터
      bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if (bluetoothAdapter == null) {
         stateLabel.setText("블루투스 지원 안함");
      } else {
         stateLabel.setText("블루투스 지원함");

         Log.d(TAG, "블루투스 어댑터 " + bluetoothAdapter);

         IntentFilter filter = new IntentFilter();
         filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
         filter.addAction(BluetoothDevice.ACTION_FOUND);
         filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
         filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
         registerReceiver(mReceiver, filter);

         ListView pairedDeviceList = (ListView) findViewById(R.id.pairedDeviceList);
         pairedDeviceListAdapter = new ArrayAdapter<DeviceInfo>(this, android.R.layout.simple_list_item_1);
         pairedDeviceList.setAdapter(pairedDeviceListAdapter);

         ListView newDeviceList = (ListView) findViewById(R.id.newDeviceList);
         newDeviceListAdapter = new ArrayAdapter<DeviceInfo>(this, android.R.layout.simple_list_item_1);
         newDeviceList.setAdapter(newDeviceListAdapter);
      }
   }

   public void startDiscover(View v) {
      if (!bluetoothAdapter.isEnabled()) {
         Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST);
         return;
      }

      if ( bluetoothAdapter.isDiscovering() ) {
         bluetoothAdapter.cancelDiscovery();
         stateLabel.setText("블루투스 장치 검색 중지");
      }
      else {
         newDeviceListAdapter.clear();
         pairedDeviceListAdapter.clear();

         // 페어링됐던 기기 목록
         Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
         Log.d(TAG, "Paried Device : " + pairedDevices.size());
         if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
               DeviceInfo info = DeviceInfo.getInfo(device);
               pairedDeviceListAdapter.add(info);
            }
         }
         pairedDeviceListAdapter.notifyDataSetChanged();

         // 주변 블루투스 기기 검색 시작
         Log.d(TAG, "Start discovery");
         bluetoothAdapter.startDiscovery();
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if ( BLUETOOTH_ENABLE_REQUEST == requestCode ) {
         if ( RESULT_OK == resultCode ) {
            stateLabel.setText("블루투스 활성화");
            // 다시 장치 검색 시작
            startDiscover(null);
         }
         else {
            stateLabel.setText("블루투스 활성화 거부");
         }
      }
   }

   // 블루투스 기기 검색 이벤트 리시버
   BroadcastReceiver mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         if ( BluetoothDevice.ACTION_FOUND.equals(action)) {
            String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            DeviceInfo info = DeviceInfo.getInfo(device);
            newDeviceListAdapter.add(info);
            newDeviceListAdapter.notifyDataSetChanged();
         }
         else if ( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            // 블루투스 기기 검색 시작
            Log.d(TAG, "블루투스 기기 검색 시작");
            stateLabel.setText("블루투스 장치 검색 시작");
         }
         else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            // 블루투스 기기 검색 끝
            Log.d(TAG, "블루투스 기기 검색 종료");
            stateLabel.setText("블루투스 기기 검색 종료");
         }
         else {
            Log.d(TAG, "what? : " + action);
         }
      }
   };

   @Override
   protected void onStop() {
      super.onStop();
      unregisterReceiver(mReceiver);
   }
}

class DeviceInfo {
   String name;
   String address;
   String type;
   String service;

   @Override
   public String toString() {
      return "name : " + name + "\naddress : " + address + "\ntype : " + type + "\nservice : " + service;
   }

   static DeviceInfo getInfo(BluetoothDevice device) {
      DeviceInfo info = new DeviceInfo();
      info.name = device.getName();
      info.address = device.getAddress();

      BluetoothClass btClass = device.getBluetoothClass();

      String type = null;

      switch (btClass.getMajorDeviceClass()) {
         case BluetoothClass.Device.Major.AUDIO_VIDEO:
            type = "AUDIO_VIDEO"; break;
         case BluetoothClass.Device.Major.COMPUTER:
            type = "COMPUTER"; break;
         case BluetoothClass.Device.Major.HEALTH:
            type = "HEALTH"; break;
         case BluetoothClass.Device.Major.IMAGING:
            type = "IMAGING"; break;
         case BluetoothClass.Device.Major.MISC:
            type = "MISC"; break;
         case BluetoothClass.Device.Major.NETWORKING:
            type = "NETWORKING"; break;
         case BluetoothClass.Device.Major.PERIPHERAL:
            type = "PERIPHERAL"; break;
         case BluetoothClass.Device.Major.PHONE:
            type = "PHONE"; break;
         case BluetoothClass.Device.Major.TOY:
            type = "TOY"; break;
         case BluetoothClass.Device.Major.UNCATEGORIZED:
            type = "UNCATEGORIZED"; break;
         case BluetoothClass.Device.Major.WEARABLE:
            type = "WEARABLE"; break;
      }

      info.type = type + " (" + btClass.getDeviceClass() + ")";

      StringBuffer buffer = new StringBuffer();
      if ( btClass.hasService(BluetoothClass.Service.NETWORKING) ) buffer.append("NETWORKING ");
      if ( btClass.hasService(BluetoothClass.Service.AUDIO) ) buffer.append("AUDIO ");
      if ( btClass.hasService(BluetoothClass.Service.CAPTURE) ) buffer.append("CAPTURE ");
      if ( btClass.hasService(BluetoothClass.Service.INFORMATION)) buffer.append("INFORMATION ");
      if ( btClass.hasService(BluetoothClass.Service.OBJECT_TRANSFER)) buffer.append("OBJECT_TRANSFER ");
      if ( btClass.hasService(BluetoothClass.Service.POSITIONING)) buffer.append("POSITIONING ");
      if ( btClass.hasService(BluetoothClass.Service.RENDER)) buffer.append("RENDER ");
      if ( btClass.hasService(BluetoothClass.Service.TELEPHONY)) buffer.append("TELEPHONY ");
      info.service = buffer.toString();

      return info;
   }
}
