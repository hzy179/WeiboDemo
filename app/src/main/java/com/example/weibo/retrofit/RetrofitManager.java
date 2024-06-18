package com.example.weibo.retrofit;

import com.example.weibo.HeaderInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static volatile RetrofitManager retrofitManager;
    private final Retrofit retrofit;

    private OkHttpClient client;

    private RetrofitManager(){
        client = new OkHttpClient
                .Builder()
                .addInterceptor(new HeaderInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://hotfix-service-prod.g.mi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public RetrofitApi createApi(){
        return retrofit.create(RetrofitApi.class);
    }


    public static RetrofitManager getInstance(){
        if (retrofitManager == null){
            synchronized (RetrofitManager.class){
                if (retrofitManager == null){
                    retrofitManager = new RetrofitManager();
                }
            }
        }
        return retrofitManager;
    }
}