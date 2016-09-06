package com.wannabewize.xml.topsongs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_NAME = "im:name";
    private static final String TAG_ARTIST = "im:artist";
    private static final String TAG_IMAGE = "im:image";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_ID = "id";


    static private String TAG = "SAXParsing";

    private ArrayList<SongInfo> songList = new ArrayList<>();
    private Handler handler = new Handler();
    private SongInfoAdapter songListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.songList);
        songListAdapter = new SongInfoAdapter();
        listView.setAdapter(songListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new XMLParseThread().start();
    }

    private String urlStr = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/xml";

    // XML 파싱 쓰레드
    class XMLParseThread extends Thread {
        @Override
        public void run() {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                URL url = new URL(urlStr);
                InputStream is = url.openConnection().getInputStream();
                parser.parse(is, topSongsParsingHandler);

                // 목록에 반영
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        songListAdapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "ParserConfigurationException", e);
                e.printStackTrace();
            }
        }
    };

    class SongInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songList.size();
        }

        @Override
        public Object getItem(int position) {
            return songList.get(position);
        }

        @Override
        public long getItemId(int position) {
            SongInfo song = songList.get(position);
            return Long.parseLong(song.id);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if ( convertView == null ) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.song_info, parent, false);
            }

            TextView nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            TextView artistLabel = (TextView) convertView.findViewById(R.id.artistLabel);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.songImage);

            SongInfo info = (SongInfo) getItem(position);
            nameLabel.setText(info.name);
            artistLabel.setText(info.artist);

            // TODO : Image 반영
            imageView.setImageResource(R.drawable.song_icon);

            return convertView;
        }
    };

    // 파싱 이벤트
    private DefaultHandler topSongsParsingHandler = new DefaultHandler() {

        Boolean mInterestingTag = false;
        StringBuffer mBuffer;
        SongInfo currentSong;

        @Override
        public void startDocument() throws SAXException {
            Log.d(TAG, "start Document");

            // 버퍼 준비
            mBuffer = new StringBuffer();
            // 기존 내용 삭제
            songList.clear();
        }

        @Override
        public void endDocument() throws SAXException {
            Log.d(TAG, "End Document");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // localName : image qName : im:image
//            Log.d(TAG, "localName : " + localName + " qName : " + qName);
            if ( TAG_ENTRY.equals(localName)) {
                currentSong = new SongInfo();
            }
            else if ( TAG_ID.equals(localName) && currentSong != null ) {
                // id 태그는 entry 밖에도 있다
                // id 태그의 값은 시작 태그의 attribute에 있다
                String id = attributes.getValue("im:id");
                currentSong.id = id;
            }
            else if ( TAG_NAME.equals(qName)
                    || TAG_ARTIST.equals(qName)
                    || ( TAG_IMAGE.equals(qName) && attributes.getValue("height").equals("55") ) ) { // 높이가 55인 이미지만 사용
                mInterestingTag = true;
            }

            if ( TAG_IMAGE.equals(localName) ) {
                Log.d(TAG, "Image attributes : " + attributes);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ( TAG_ENTRY.equals(localName) ) {
                // </entry> 태그에서는 음악 개별 정보가 완성된다.
                songList.add(currentSong);
                currentSong = null;
            }
            else if ( TAG_NAME.equals(qName)  ) {
                // name 태그는 외부에도 있다. qName인 im:name으로 구분
                // 음악 이름을 현재 분석 중인 음악 정보에 설정
                currentSong.name = mBuffer.toString();
            }
            else if ( TAG_ARTIST.equals(qName) ) {
                // 가수 이름을 현재 분석 중인 음악 정보에 설정
                currentSong.artist = mBuffer.toString();
            }
            else if ( TAG_IMAGE.equals(qName) && mInterestingTag ) {
                // 이미지
                currentSong.image = mBuffer.toString();
            }

            // 버퍼 지우기
            if ( mInterestingTag ) {
                mInterestingTag = false;
                mBuffer.delete(0, mBuffer.length());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            // 태그 내 데이터가 한번에 전달되지 않으므로 버퍼링 한다
            if (mInterestingTag) {
                String tmp = new String(ch, start, length);
                mBuffer.append(tmp);
            }
        }
    };
}
