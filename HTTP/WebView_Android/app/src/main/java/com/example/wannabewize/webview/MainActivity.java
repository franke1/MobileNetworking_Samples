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
                Log.d(TAG, "Error(" + errorCode + ") desc : " + description);
                Toast.makeText(MainActivity.this, "Error : " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view, url);

                Log.d(TAG, "on Page Finished : " + url);
            }
        });

        mWebView.loadUrl("http://google.com");
        mEditText = (EditText)findViewById(R.id.editText);

        findViewById(R.id.goButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
    }

    public void go() {
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
