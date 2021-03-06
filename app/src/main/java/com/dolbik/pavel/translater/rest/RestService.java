package com.dolbik.pavel.translater.rest;


import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestService {

    private static volatile RestService instance;

    private RestService() {}

    public static RestService getInstance() {
        if (instance == null) {
            synchronized (RestService.class) {
                if (instance == null) {
                    instance = new RestService();
                }
            }
        }
        return instance;
    }


    private OkHttpClient.Builder getOkHttpBuilder(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder
                //.addInterceptor(logging) //Для отладки запросов
                .addInterceptor(new ApiKeyInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(   10, TimeUnit.SECONDS)
                .writeTimeout(  10, TimeUnit.SECONDS)
                .cache(cache)
                .followRedirects(false);
    }


    private Retrofit getRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpBuilder(context).build())
                .build();
    }


    public RestApi getRestApi(Context context) {
        return getRetrofit(context).create(RestApi.class);
    }

}
