package com.vanillastepdev.example.shoppingmall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProductDetailActivity extends AppCompatActivity {

   private TextView mTextView;
   private String productCode;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_product_detail);

      mTextView = (TextView)findViewById(R.id.textView);

      productCode = getIntent().getStringExtra("ProductCode");
      mTextView.setText("제품 상세 정보 보기. ProductCode : "+productCode);
   }
}
