package com.example.wannabewize.webview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WebViewApp";

    private WebView mWebView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Random random = new Random();
                int r = random.nextInt(10);
                if ( r > 3 ) {
                    return true;
                }
                else {
                    Toast.makeText(MainActivity.this, "로딩 금지 불행 당첨!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view, url);

                Log.d(TAG, "on Page Finished : " + url);
            }
        });

        mWebView.loadUrl("http://google.com");
        mEditText = (EditText)findViewById(R.id.editText);
    }

    public void go(View v) {
        String url = mEditText.getText().toString();
        Log.d(TAG, "load url : " + url);
        mWebView.loadUrl(url);
    }

    public void stopLoading(View v) {
        mWebView.stopLoading();
    }

    public void goBack(View v) {
        mWebView.goBack();
    }

    public void goForward(View v) {
        mWebView.goForward();
    }

    public void reload(View v) {
        mWebView.reload();
    }

}
