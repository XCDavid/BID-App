package com.teknei.bid.ws;

import com.teknei.bid.services.BIDEndPointServices;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Instancia para configuración de Retrofit
 */
public class RetrofitSingleton {

    public static String BASE_URL = "http://192.168.1.190:28080";
    private static RetrofitSingleton instance;
    private static int TIMEOUT_SECONDS = 120;

    private RetrofitSingleton() {
        // build retrofit singleton
    }

    public static Retrofit build(String baseURL) {
        OkHttpClient httpClient = buildHttpClient();
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .validateEagerly(true)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    private static OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public static RetrofitSingleton getInstance() {
        if (instance == null) {
            instance = new RetrofitSingleton();
        }
        return instance;
    }

    public static Retrofit getRetrofit() {
        return buildRetrofitInstance(BASE_URL);
    }

    public static Retrofit getRetrofitByUrl(String baseUrl) {
        return buildRetrofitInstance(baseUrl);
    }

    private static Retrofit buildRetrofitInstance(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .validateEagerly(true)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClientInstance())
                .build();
        return retrofit;
    }

    public static BIDEndPointServices build() {
        return getRetrofit().create(BIDEndPointServices.class);
    }

    private static OkHttpClient getOkHttpClientInstance() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        return client;
    }

}