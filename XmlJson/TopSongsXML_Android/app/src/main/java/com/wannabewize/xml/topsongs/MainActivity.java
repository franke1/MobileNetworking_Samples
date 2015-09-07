package com.wannabewize.xml.topsongs;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends ActionBarActivity {

    static private String TAG = "SAXParsing";

    private ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> songList = new ArrayList<>();
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.songList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
        
        listView.setAdapter(adapter);
        parseThread.start();
    }

    private String urlStr = "http://ax.itunes.apple.com/WebObjects/MZStoreServices" +
            ".woa/ws/RSS/topsongs/limit=25/xml";

    // XML 파싱 쓰레드
    private Thread parseThread = new Thread() {
        @Override
        public void run() {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                URL url = new URL(urlStr);
                InputStream is = url.openConnection().getInputStream();
                parser.parse(is, MainActivity.this.parsingHandler);
            } catch (ParserConfigurationException e) {
                Log.e(TAG, "ParserConfigurationException", e);
                e.printStackTrace();
            } catch (SAXException e) {
                Log.e(TAG, "SAXException", e);
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException", e);
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            }
        }
    };

    // 파싱 이벤트
    private DefaultHandler parsingHandler = new DefaultHandler() {

        Boolean mInterestingTag = false;
        StringBuffer mBuffer;

        @Override
        public void startDocument() throws SAXException {
            Log.d(TAG, "start Document");

            // 기존 내용 지우기
            mBuffer = new StringBuffer();
        }

        @Override
        public void endDocument() throws SAXException {
            Log.d(TAG, "End Document");
            // 목록에 반영
            handler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            Log.d(TAG, "Start Element : " + localName);
            if ( "title".equals(localName)) {
                mInterestingTag = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ( "title".equals(localName)) {
                Log.d(TAG, "End Element : " + mBuffer.toString() + " item count : " + MainActivity.this
                        .adapter.getCount());
                songList.add(mBuffer.toString());
            }

            // 버퍼 지우기
            if ( mBuffer.length() > 0 )
                mBuffer.delete(0, mBuffer.length());

            mInterestingTag = false;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (mInterestingTag) {
                // 버퍼를 이용해서 파싱 중인 문자열 덧붙이기
                String tmp = new String(ch, start, length);
                mBuffer.append(tmp);
                Log.d(TAG, "buffer : " + tmp);
            }
        }
    };
}
