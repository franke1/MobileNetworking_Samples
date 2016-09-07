package com.vanillastep.example.moviest;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientSingleton {

    private final OkHttpClient httpClient;

    // 생성자 직접 호출 방지 : private
    private OkHttpClientSingleton() {
        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    // Singleton을 위한 static
    private static OkHttpClientSingleton mInstance;
    public static synchronized OkHttpClientSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpClientSingleton();
        }
        return mInstance;
    }

}
