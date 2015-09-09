package com.vanillastep.example.btle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "BLTE_Sample";
   private BluetoothLeScanner mScanner;

   List<ScanResult> deviceList = new ArrayList<>();
   DeviceAdapter deviceAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      ListView listView = (ListView) findViewById(R.id.listView);
      deviceAdapter = new DeviceAdapter();
      listView.setAdapter(deviceAdapter);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScanResult result = (ScanResult) deviceAdapter.getItem(position);
            BluetoothDevice device = result.getDevice();

            connectDevice(device);
         }
      });

      if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
         Toast.makeText(this, "블루투스 LE 지원 안함", Toast.LENGTH_SHORT).show();
         finish();
      }

      BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      mScanner = mBluetoothAdapter.getBluetoothLeScanner();
   }

   void connectDevice(BluetoothDevice device) {
      String name = device.getName();
      String address = device.getAddress();

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

   public void startDiscover(View v) {
      deviceList.clear();

      mScanner.startScan(new ScanCallback() {
         @Override
         public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, result.toString());

            deviceList.add(result);
            deviceAdapter.notifyDataSetChanged();
         }

         @Override
         public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
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
