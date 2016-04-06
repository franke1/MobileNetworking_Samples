package com.vanillastepdev.example.shoppingmall;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

   static final public String APP_KEY = "API-KEY";
   private static final String TAG = "OpenAPI-Sample";
   private ListView mListView;
   private EditText mKeywordInput;

   private Handler handler = new Handler();

   List<ProductInfo> productInfoList = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mListView = (ListView) findViewById(R.id.listView);

      mListView.setAdapter(mAdapter);
      mKeywordInput = (EditText) findViewById(R.id.editText);

      findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String keyword = mKeywordInput.getText().toString();
            new ProductSearchThread(keyword).start();
         }
      });

      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ProductInfo product = (ProductInfo) mAdapter.getItem(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("ProductCode", product.code);

            MainActivity.this.startActivity(intent);
         }
      });

   }

   // 제품 검색 API 호출 쓰레드
   class ProductSearchThread extends Thread {
      String keyword;

      ProductSearchThread(String keyword) {
         this.keyword = keyword;
      }

      @Override
      public void run() {

         productInfoList.clear();
         try {
            // URLEncoder는 URL을 구성하는 요소(://) 도 인코딩하므로, 인코딩이 필요한 부분만 인코딩한다.
            String encoded = URLEncoder.encode(keyword, "UTF-8");
            String urlStr = "http://apis.skplanetx.com/11st/common/products?version=1&xml=&appKey=" +
                    APP_KEY + "&searchKeyword=" + encoded;
            Log.d(TAG, "url : " + urlStr);

            URL url = new URL(urlStr);
            InputStream is = url.openStream();

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, xmlParseHandler);

         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   // 리스트 뷰의 아답터
   BaseAdapter mAdapter = new BaseAdapter() {
      @Override
      public int getCount() {
         return productInfoList.size();
      }

      @Override
      public Object getItem(int position) {
         return productInfoList.get(position);
      }

      @Override
      public long getItemId(int position) {
         return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
         if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.produce_item_layout, parent, false);
         }

         ProductInfo product = (ProductInfo) getItem(position);
         TextView nameLabel = (TextView) convertView.findViewById(R.id.productName);
         nameLabel.setText(product.name);

         return convertView;
      }
   };

   // XML 파서의 파싱 이벤트 핸들러
   DefaultHandler xmlParseHandler = new DefaultHandler() {
      boolean interestingTag;
      ProductInfo currentProduct;

      final static String PRODUCT_TAG = "Product";
      final static String CODE_TAG = "ProductCode";
      final static String NAME_TAG = "ProductName";
      final static String IMAGE_TAG = "ProductImage";

      @Override
      public void endDocument() throws SAXException {
         handler.post(new Runnable() {
            @Override
            public void run() {
               MainActivity.this.mAdapter.notifyDataSetChanged();
            }
         });
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         switch (localName) {
            case PRODUCT_TAG:
               currentProduct = new ProductInfo();
               break;
            case CODE_TAG:
            case NAME_TAG:
            case IMAGE_TAG:
               interestingTag = true;
         }
      }

      @Override
      public void endElement(String uri, String localName, String qName) throws SAXException {

         switch (localName) {
            case PRODUCT_TAG:
               productInfoList.add(currentProduct);
               currentProduct = null;
               break;
            case CODE_TAG:
               currentProduct.code = mBuffer.toString();
               break;
            case NAME_TAG:
               currentProduct.name = mBuffer.toString();
               break;
            case IMAGE_TAG:
               currentProduct.image = mBuffer.toString();
         }

         // 버퍼 비우기
         if (interestingTag) {
            mBuffer.delete(0, mBuffer.length());
            interestingTag = false;
         }
      }

      StringBuffer mBuffer = new StringBuffer();

      @Override
      public void characters(char[] ch, int start, int length) throws SAXException {
         if (interestingTag)
            mBuffer.append(ch, 0, length);

      }
   };

   // 제품 정보 클래스
   class ProductInfo {
      String code, name, image;
   }
}
