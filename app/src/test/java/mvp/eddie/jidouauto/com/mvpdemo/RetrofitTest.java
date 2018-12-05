package mvp.eddie.jidouauto.com.mvpdemo;


import com.jidouauto.eddie.mvpdemo.network.RetrofitClient;

import org.junit.Test;

import java.util.HashMap;

import io.reactivex.observers.TestObserver;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.jidouauto.eddie.mvpdemo.api.ApiService.API;

/**
 * The type Retrofit test.
 */
public class RetrofitTest {

    /**
     * Test http.
     */
    @Test
    public void testHttp() {

        TestObserver<String> testObserver = new TestObserver<>();
        API.home().subscribe(testObserver);
        testObserver.assertValueCount(1);

        TestObserver<String> testObserver2 = new TestObserver<>();
        API.home().subscribe(testObserver2);

        testObserver2.assertValueCount(1);
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
