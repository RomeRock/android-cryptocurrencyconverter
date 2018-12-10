package com.romerock.apps.utilities.cryptocurrencyconverter.api;

/**
 * Created by Ebricko on 20/04/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static String BASE_URL_ROMEROCK ="https://api.romerock.com/";
    private static String APIKEY ="LOFLDWCPLJMUPHRGBDTWLYCY";
    private static String SOURCE_CODE="currency";
    private static String SOURCE_LOC="currency.com";
    private static String OS="android";

    public static String getOS() {
        return OS;
    }

    public static String getAPIKEY() {
        return APIKEY;
    }

    public static String getSourceCode() {
        return SOURCE_CODE;
    }

    public static String getSourceLoc() {
        return SOURCE_LOC;
    }

    private static Retrofit retrofit = null;
    private static Retrofit retrofitHotPolls = null;
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    public static Retrofit getApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_ROMEROCK)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

}