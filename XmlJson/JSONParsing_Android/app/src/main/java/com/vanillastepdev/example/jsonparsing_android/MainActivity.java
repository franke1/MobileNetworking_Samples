package com.vanillastepdev.example.jsonparsing_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "JSONExample";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // greeting.json 사용하는 예제. who / name 값 얻어오기
//      parseByJSONObject();
//      new JsonReaderThread().start();
//      parseByGson1();
//      makeGreetingJSON();

      // topsongs.json 사용 예제
      parseTopSongsByGson();
   }

   // JSONObject로 파싱
   void parseByJSONObject() {
      try {
         InputStream is = getAssets().open("greeting.json");
         BufferedReader reader = new BufferedReader(new InputStreamReader(is));
         StringBuffer jsonStr = new StringBuffer();
         String line;
         while ( ( line = reader.readLine()) != null ) {
            jsonStr.append(line);
         }
         JSONObject root = new JSONObject(jsonStr.toString());
         JSONObject who = root.getJSONObject("who");
         String name = who.getString("name");
         Log.d(TAG, "name is " + name);

      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   // I/O 동작도 블록 방식이므로 비동기 방식으로
   class JsonReaderThread extends Thread {
      @Override
      public void run() {
         try {
            Log.d(TAG, "Parsing JSON with JsonReader");
            InputStream is = getAssets().open("greeting.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            reader.beginObject(); // Root는 객체
            while ( reader.hasNext() ) {
               String name = reader.nextName();
               Log.d(TAG, "next name : " + name);
               if ("who".equals(name) ) {
                  Log.d(TAG, "Found who");
                  reader.beginObject();
                  if ( "name".equals(reader.nextName()) ) {
                     String goal = reader.nextString();
                     Log.d(TAG, "name is " + goal);
                  }
               }
               else {
                  reader.skipValue();
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   void parseByGson1() {
      try {
         Gson gson = new Gson();
         InputStream is = getAssets().open("greeting.json");
         InputStreamReader reader = new InputStreamReader(is);

         Greeting greeting = gson.fromJson(reader, Greeting.class);
         String whoName = greeting.who.name;
         Log.d(TAG, "Greeting to " + whoName);
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   void makeGreetingJSON() {
      Greeting obj = new Greeting();
      obj.greeting = "Good night";
      obj.when = "11 pm";
      obj.who = new Who();
      obj.who.name = "IU";

      Gson gson = new Gson();
      String jsonStr = gson.toJson(obj);
      Log.d(TAG, "toJSON : " + jsonStr);
   }

   class Greeting {
      String greeting;
      String when;
      Who who;
   }

   class Who {
      String name;
   }

   void parseTopSongsByGson() {
      try {
         Gson gson = new Gson();
         InputStream is = getAssets().open("topsongs.json");
         InputStreamReader reader = new InputStreamReader(is);

         TopSongs topSongs = gson.fromJson(reader, TopSongs.class);
         for ( Entry entry : topSongs.feed.entry ) {
            Log.d(TAG, entry.name + " - " + entry.artist );
         }
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   class TopSongs {
      Feed feed;
   }

   class Feed {
      List<Entry> entry = new ArrayList<>();
   }

   class Entry {
      String name;
      String artist;
      String image;
   }
}
