package com.romerock.apps.utilities.cryptocurrencyconverter.api;

import com.romerock.apps.utilities.cryptocurrencyconverter.model.CurrencyResponseModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by learn-zone on 18/3/17.
 */


public interface ApiConfig {

    @GET("/v1/cryptocurrency/history/json/get")
    Call<CurrencyResponseModel> getCurrencyCompare(
            @Query("apikey") String apikey,
            @Query("fromsymbol") String fromsymbol,
            @Query("tosymbol") String tosymbol,
            @Query("sourceloc") String sourceloc,
            @Query("sourcecode") String sourcecode,
            @Query("range") String range,
            @Query("graph") int graph
    );

    @GET("/v1/firebase/feedback/json/post")
    Call<ResponseModel> sendMail(
            @Query("apikey") String apikey,
            @Query("sourcecode") String sourcecode,
            @Query("message") String message,
            @Query("sourceloc") String sourceloc,
            @Query("os") String os,
            @Query("appname") String appname,
            @Query("email") String email
    );


}
