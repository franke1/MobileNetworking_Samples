package com.vanillastep.example.btle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "BLTE_Sample";
   private BluetoothLeScanner mScanner;
   private BluetoothAdapter mBluetoothAdapter;
   private boolean mScanning; // 상태값이 없다.

   List<ScanResult> deviceList = new ArrayList<>();
   DeviceAdapter deviceAdapter;
   private static final long SCAN_PERIOD = 10000;
   private TextView mStatusView;

   private Handler mHandler;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mHandler = new Handler();

      if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
         Toast.makeText(this, "블루투스 LE 지원 안함", Toast.LENGTH_SHORT).show();
         finish();
      }

      Button discoverButton = (Button) findViewById(R.id.discoverButton);
      discoverButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            toggleScan();
         }
      });

      mStatusView = (TextView) findViewById(R.id.statusView);

      ListView listView = (ListView) findViewById(R.id.listView);
      deviceAdapter = new DeviceAdapter();
      listView.setAdapter(deviceAdapter);
      // 리스트 선택 - 연결
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScanResult result = (ScanResult) deviceAdapter.getItem(position);
            BluetoothDevice device = result.getDevice();
            connectDevice(device);
         }
      });

      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      mScanner = mBluetoothAdapter.getBluetoothLeScanner();
   }

   void connectDevice(BluetoothDevice device) {
      Log.d(TAG, "연결 시도 : " + device.getName());

      device.connectGatt(MainActivity.this, false, new BluetoothGattCallback() {
         @Override
         public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
               case BluetoothProfile.STATE_DISCONNECTED:
                  Log.d(TAG, "onConnectionStateChange : STATE_DISCONNECTED");
                  break;
               case BluetoothProfile.STATE_CONNECTING:
                  Log.d(TAG, "onConnectionStateChange : STATE_CONNECTING");
                  break;
               case BluetoothProfile.STATE_CONNECTED:
                  Log.d(TAG, "onConnectionStateChange : STATE_CONNECTED");
                  gatt.discoverServices();
                  break;
               case BluetoothProfile.STATE_DISCONNECTING:
                  Log.d(TAG, "onConnectionStateChange : STATE_CONNECTED");
                  break;
            }
            Log.d(TAG, "onConnectionStateChange : " + newState);
         }

         @Override
         public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead : ");
         }

         @Override
         public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
         }

         @Override
         public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            List<BluetoothGattService> gattServices = gatt.getServices();

            for (BluetoothGattService service : gattServices) {
               // 서비스의 UUID
               String serviceUuid = service.getUuid().toString();
               List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

               for ( BluetoothGattCharacteristic characteristic : characteristics ) {
                  String characteristicUuid = characteristic.getUuid().toString();
                  String value = characteristic.getStringValue(0);

                  Log.d(TAG, "uuid : " + characteristicUuid + " value : " + value);
               }
            }
         }
      });
   }

   void toggleScan() {
      if ( mScanning == true ) {
         mScanner.stopScan(null);
         mScanning = false;
         mStatusView.setText("스캐닝 중지");
      }
      else {
         startScan();
         mScanning = true;
         mStatusView.setText("스캐닝 시작");

         // 10초뒤 자동 스캐닝 중지
         mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if ( mScanning ) {
                  toggleScan();
               }
            }
         }, 10 * 1000);
      }
   }

   // LE 장치 검색
   void startScan() {
      deviceList.clear();
      deviceAdapter.notifyDataSetChanged();

      // from API 21
      mScanner.startScan(new ScanCallback() {
         @Override
         public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            deviceList.add(result);
            deviceAdapter.notifyDataSetChanged();
         }

         @Override
         public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            mHandler.post(new Runnable() {
               @Override
               public void run() {
                  mStatusView.setText("스캐닝 에러 : " + errorCode);
               }
            });
         }

         @Override
         public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
         }
      });
   }

   class DeviceAdapter extends BaseAdapter {

      @Override
      public int getCount() {
         return deviceList.size();
      }

      @Override
      public Object getItem(int position) {
         return deviceList.get(position);
      }

      @Override
      public long getItemId(int position) {
         return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
         if ( null == convertView ) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_cell_layout, parent, false);
         }

         ScanResult result = (ScanResult) getItem(position);

         TextView nameLabel = (TextView)convertView.findViewById(R.id.nameLabel);
         nameLabel.setText("이름 : " + result.getDevice().getName());

         TextView infoView = (TextView) convertView.findViewById(R.id.infoView);
         infoView.setText(result.toString());

         return convertView;
      }
   }
}

