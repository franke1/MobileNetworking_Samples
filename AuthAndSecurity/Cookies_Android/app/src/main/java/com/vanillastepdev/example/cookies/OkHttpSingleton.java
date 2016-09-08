package com.vanillastepdev.example.cookies;

import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by wannabewize on 2016. 9. 8..
 */
public class OkHttpSingleton {
    private static OkHttpSingleton _instance = null;
    static OkHttpSingleton sharedInstance() {
        if ( _instance == null ) {
            _instance = new OkHttpSingleton();
        }
        return _instance;
    }

    private OkHttpSingleton() {
        // For Persistent Cookie - https://github.com/franmontiel/PersistentCookieJar

        CookieManager cookieManager = new CookieManager();

        // okhttp-urlconnection
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public OkHttpClient httpClient;
}
