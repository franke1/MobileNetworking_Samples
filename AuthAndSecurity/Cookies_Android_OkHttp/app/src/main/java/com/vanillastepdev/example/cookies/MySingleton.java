package com.vanillastepdev.example.cookies;

import java.net.CookieHandler;
import java.net.CookieManager;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;


class MySingleton {
    private static MySingleton _instance = null;
    static MySingleton sharedInstance() {
        if ( _instance == null ) {
            _instance = new MySingleton();
        }
        return _instance;
    }

    private MySingleton() {
        // For Persistent Cookie - https://github.com/franmontiel/PersistentCookieJar
        CookieHandler cookieHandler = new CookieManager();
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieHandler);
        httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public OkHttpClient httpClient;
}
