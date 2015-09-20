package com.vanillastep.example.networkinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "NetworkInfo-Sample";
   private TextView infoText;
   private Switch wifiSwitch;
   // 상태 변경 모니터링
   private TextView stateChangeLog;
   private StateChagneMonitor stateChagneMornitor;
   private TextView reachabilityResult;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      infoText = (TextView) findViewById(R.id.infoText);

      wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
      wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            changeWifiStatus(b);
         }
      });
      stateChangeLog = (TextView) findViewById(R.id.stateChangeLog);
      reachabilityResult = (TextView)findViewById(R.id.reachabilityResult);
   }

   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         if ( msg.what == 1 ) {
            String str = (String) msg.obj;
            reachabilityResult.setText(str);
         }
      }
   };

   public void showNetworkInfo(View v) {
      String infoStr = null;

      ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo info = service.getActiveNetworkInfo();
      if (info == null) {
         infoStr = "활성화된 네트워크 없음";
      } else {
         infoStr = "연결 타입 : " + info.getTypeName() + "\n";
         Log.d(TAG, "Network Info, name : " + info.getTypeName());

         infoStr += "연결 상태 : ";
         if (info.isConnectedOrConnecting()) {
            infoStr += "연결됨(중)\n";
         } else {
            infoStr += "연결 안됨\n";
         }
      }

      infoText.setText(infoStr);
   }

   public void checkReachability(View v) {
      EditText editText = (EditText) findViewById(R.id.address);
      String address = editText.getText().toString();
      reachabilityResult.setText("연결 테스트 중");
      new ReachableTestThread(address).start();
   }

   class ReachableTestThread extends Thread {
      String host;
      ReachableTestThread(String host) {
         this.host = host;
      }
      @Override
      public void run() {
         String result = "연결 테스트 실패";
         try {

            InetAddress addresses[] = InetAddress.getAllByName(host);
            for ( InetAddress address : addresses ) {
               if ( address.isReachable(10 * 1000) ) {
                  Log.d(TAG, "Success : " + address.getHostAddress());
                  result = "연결 테스트 성공";
                  break;
               }
               else {
                  Log.d(TAG, "연결 테스트 실패 : " + address.getHostAddress());
               }
            }

            /*
            // 거의 동작 안함
            InetAddress inetAddress = InetAddress.getByName(host);
            boolean ret = inetAddress.isReachable(30 * 1000); // 30초 동안 확인
            if ( ret ) {
               result = "연결 테스트 성공";
            }
            */
         } catch (Exception e) {
            Log.e(TAG, "Exception", e);
            result = "연결 테스트 에러 " + e.getLocalizedMessage();
         }

         Message msg = new Message();
         msg.what = 1;
         msg.obj = result;
         handler.sendMessage(msg);
      }
   }

   void updateWifiSwitch() {
      // Wifi 상태 반영하기
      ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo wifiInfo = service.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      if (wifiInfo != null) {
         wifiSwitch.setChecked(wifiInfo.isConnected());
      }
   }

   void changeWifiStatus(Boolean b) {
      WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
      // CHANGE_WIFI_STATE 퍼미션 필요
      wifiManager.setWifiEnabled(b);
   }

   // 상태 변경 모니터링
   class StateChagneMonitor extends BroadcastReceiver {
      @Override
      public void onReceive(Context context, Intent intent) {
         ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, 0);
         NetworkInfo eventNetwork = service.getNetworkInfo(networkType);
         stateChangeLog.append("이벤트 발생 장치 : " + eventNetwork.getTypeName());
         if (eventNetwork.isConnectedOrConnecting())
            stateChangeLog.append(" 연결됨(중)\n");
         else
            stateChangeLog.append(" 연결 안됨\n");

         boolean b = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
         stateChangeLog.append("FAILOVER : " + b + "\n");

         // 활성 상태의 네트워크 장치
         NetworkInfo activeInfo = service.getActiveNetworkInfo();
         if (activeInfo != null) {
            stateChangeLog.append("활성화된 네트워크 : " + activeInfo.getTypeName());
            if (activeInfo.isConnectedOrConnecting())
               stateChangeLog.append(" 연결됨(중)\n");
            else
               stateChangeLog.append(" 연결 안됨\n");
         }

         String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
         if (reason != null)
            stateChangeLog.append("EXTRA REASON : " + reason + "\n");

         boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
         if (noConnectivity == true)
            stateChangeLog.append("가능한 네트워크 없음\n");

         updateWifiSwitch();

         stateChangeLog.append("============================\n");
      }
   }

   @Override
   protected void onResume() {
      super.onResume();

      // 상태 변경 모니터링
      IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
      stateChagneMornitor = new StateChagneMonitor();
      registerReceiver(stateChagneMornitor, filter);
   }

   @Override
   protected void onPause() {
      super.onPause();
      unregisterReceiver(stateChagneMornitor);
   }
}
