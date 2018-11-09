package com.jidouauto.lib.middleware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * The interface Test api.
 */
public interface TestApi {

    /**
     * Home observable.
     *
     * @return the observable
     */
    @GET("/")
    Single<String> home();

    /**
     * Fetch nearby park list observable.
     *
     * @param params the params
     * @return the observable
     */
    @POST("park/listinfo")
    Single<String> fetchNearbyParkList(@Body HashMap<String,Object> params);
}
