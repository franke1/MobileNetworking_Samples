package com.vanillastepdev.example.topsongsdom;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "DomParse-Sample";

   // 배열과 아답터
   private ArrayAdapter<String> adapter;
   private ArrayList<String> songList = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      ListView listView = (ListView) findViewById(R.id.listView);
      adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
      listView.setAdapter(adapter);
   }

   Handler handler = new Handler();

   @Override
   protected void onResume() {
      super.onResume();
      songList.clear();

      new NetworkThread().start();
   }

   // XML Parsing 쓰레드
   class NetworkThread extends Thread {
      @Override
      public void run() {
         try {
            String urlStr = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/xml";

            // XML DOM 파서 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 파싱
            Document root = builder.parse(urlStr);

            // 루트의 자식 노드
            NodeList rootChildren = root.getChildNodes();

            // feed tag
            Node feed = rootChildren.item(0);
            NodeList feedChildren = feed.getChildNodes();

            for ( int i = 0 ; i < feedChildren.getLength() ; i++ ) {
               Node node = feedChildren.item(i);
               String nodeName = node.getNodeName();
               Log.d(TAG, "node name : " + nodeName);

               // TAG 비교 - entry 만 사용
               if (nodeName.equalsIgnoreCase("entry")) {
                  NodeList entryChildren = node.getChildNodes();

                  for ( int j = 0 ; j < entryChildren.getLength() ; j++ ) {
                     Node songInfoNode = entryChildren.item(j);

                     // entry 태그 내 title 노드 찾기
                     if ( songInfoNode.getNodeName().equalsIgnoreCase("title") ) {

                        // title 노드에서 Value 얻기
                        Node titleNode = songInfoNode.getFirstChild();
                        String title = titleNode.getNodeValue();

                        // 목록에 반영
                        songList.add(title);
                        Log.d(TAG, "Title : " + songInfoNode.getFirstChild().getNodeValue());
                     }
                  }
               }
            }
            handler.post(new Runnable() {
               @Override
               public void run() {
                  adapter.notifyDataSetChanged();
               }
            });
         } catch (Exception e) {
            Log.d(TAG, "Parsing Error", e);
         }
      }
   }
}
