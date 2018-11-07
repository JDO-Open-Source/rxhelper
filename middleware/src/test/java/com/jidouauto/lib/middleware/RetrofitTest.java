package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.network.RetrofitClient;

import org.junit.Test;

import java.util.HashMap;

import io.reactivex.observers.TestObserver;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * The type Retrofit test.
 */
public class RetrofitTest {

    /**
     * Test http.
     */
    @Test
    public void testHttp() {

        RetrofitClient retrofitClient = new RetrofitClient("http://www.baidu.com", HttpLoggingInterceptor.Level.BODY);

        TestObserver<String> testObserver = new TestObserver<>();

        retrofitClient
                .create(TestApi.class)
                .home()
                .subscribe(testObserver);

        testObserver.assertValueCount(1);
    }

    /**
     * Test https.
     */
    @Test
    public void testHttps() {

        RetrofitClient retrofitClient = new RetrofitClient("https://www.baidu.com", HttpLoggingInterceptor.Level.BODY);

        TestObserver<String> testObserver = new TestObserver<>();

        retrofitClient
                .create(TestApi.class)
                .home()
                .subscribe(testObserver);

        testObserver.assertValueCount(1);
    }

    /**
     * Test post.
     */
    @Test
    public void testPost() {
        TestObserver<String> testObserver = new TestObserver<>();

        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("lat", "39.985348");
            put("lon", "116.499171");
            put("radius", 5000);
        }};

        new RetrofitClient("http://parking.test.jidouauto.com/", HttpLoggingInterceptor.Level.BODY)
                .create(TestApi.class)
                .fetchNearbyParkList(params)
                .subscribe(testObserver);
        testObserver.assertValueCount(1);
    }
}
