package com.vanillastepdev.example.cookies;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;


public class NetworkSingleton {
    // OKHttp
    public OkHttpClient httpClient;
    // Volley
    public RequestQueue requestQueue;


    // For Singleton
    private static Context mContext;
    private static NetworkSingleton mInstance;
    public static synchronized NetworkSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkSingleton(context);
        }
        return mInstance;
    }

    // 직접 객체 생성을 막기 위해서 private로 설정
    private NetworkSingleton(Context context) {
        mContext = context;

        // Volley - HttpUrlConnection의 쿠키 설정을 사용한다
        requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        // OkHTTP 용 쿠키 매니저 설정
        // For Persistent Cookie - https://github.com/franmontiel/PersistentCookieJar

        // okhttp-urlconnection
        httpClient = new OkHttpClient.Builder().build();
    }

    public void setupCookiesStore() {

        // HttpURLConnection 쿠키 매니저 설정
        // 기본 Volley 객체로 생성하면 HttpUrlConnection을 사용한다
        CookieManager cookieManager = new CookieManager();
        CookieHandler.getDefault().setDefault(cookieManager);

        // OkHTTP 용 쿠키 매니저 설정
        // For Persistent Cookie - https://github.com/franmontiel/PersistentCookieJar
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager); // okhttp-urlconnection
        httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }
}
