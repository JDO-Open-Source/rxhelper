package com.jidouauto.eddie.mvpdemo.api;

import java.util.HashMap;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @GET("/")
    Single<String> home();

    @POST("park/listinfo")
    Single<String> fetchNearbyParkList(@Body HashMap<String,Object> params);
}
