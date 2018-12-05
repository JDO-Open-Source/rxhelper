package com.jidouauto.eddie.mvpdemo.api;

import com.jidouauto.eddie.mvpdemo.network.RetrofitClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class ApiService {

    public static final String BASE_URL = "http://www.baidu.com";

    public static RetrofitClient retrofitClient = new RetrofitClient(BASE_URL, HttpLoggingInterceptor.Level.BODY);

    public static final Api API = create();


    public static <T> T create(Retrofit retrofit, final Class<T> service) {
        return retrofit.create(service);
    }

    public static <T> T create(final Class<T> service) {
        return retrofitClient.getRetrofit().create(service);
    }

    public static Api create() {
        return create(retrofitClient.getRetrofit(), Api.class);
    }
}
