package com.jidouauto.lib.rxhelper;

import com.jidouauto.lib.rxhelper.backoff.FixedBackOffStrategy;
import com.jidouauto.lib.rxhelper.transformer.DataTransformers;
import com.jidouauto.lib.rxhelper.transformer.RetryTransformers;
import com.jidouauto.lib.rxhelper.transformer.ValidateTransformers;

import org.junit.Before;

import java.net.ConnectException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;

/**
 * The type Stream transformer test.
 */
public class TransformersTest {

    /**
     * 把异步变成同步，方便测试
     */
    @Before
    public void setSchedulerBefore() {
        // reset()不是必要，实践中发现不写reset()，偶尔会出错，所以写上保险^_^
//        RxJavaPlugins.reset();
//        RxJavaPlugins.setComputationSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
//        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
//        RxJavaPlugins.setNewThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
//        RxJavaPlugins.setSingleSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
//        RxAndroidPlugins.reset();
//        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    class NetworkException extends Exception {

    }

    class NetworkStatus {

        boolean networkConnected;

        void connect() {
            networkConnected = true;
        }

        void disconnect() {
            networkConnected = false;
        }

    }

    final String result = "hello,i'm from server!";

    class Api {
        NetworkStatus networkStatus;

        public Api(NetworkStatus networkStatus) {
            this.networkStatus = networkStatus;
        }

        String requestData() throws NetworkException {
            System.out.println("=====>start request data!");
            if (networkStatus.networkConnected) {
                System.out.println("<=====request data succeed:" + result);
                return result;
            } else {
                System.out.println("<=====request data failed:NetworkException\n");
                throw new NetworkException();
            }
        }

    }

    private Observable fixNetwork(NetworkStatus networkStatus, long delay) {
        return Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnNext(i -> networkStatus.connect());
    }


    private Observable requestData(Api api) {
        return Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return api.requestData();
                    }
                });
    }

    /**
     * Test transformer.
     *
     * @throws InterruptedException the interrupted exception
     */
    @org.junit.Test
    public void testTransformer() throws Exception {
        NetworkStatus networkStatus = new NetworkStatus();
        Api api = new Api(networkStatus);

        System.out.println("start case1 : no retry");
        networkStatus.disconnect();
        TestObserver<String> testObserver1 = new TestObserver<>();
        requestData(api).subscribe(testObserver1);
        testObserver1.awaitTerminalEvent();
        testObserver1.assertError(NetworkException.class);
        System.out.println("case1 : NetworkException\n\n");

        System.out.println("start case2 : retry but only delay 100ms");
        networkStatus.disconnect();
        TestObserver<String> testObserver2 = new TestObserver<>();
        requestData(api)
                .compose(RetryTransformers.retryOnError(3, 100, NetworkException.class))
                .subscribe(testObserver2);
        testObserver2.awaitTerminalEvent();
        testObserver2.assertError(NetworkException.class);
        System.out.println("case2 : retry failed,still NetworkException\n\n");

        System.out.println("start case3 : retry,delay 100ms,fixNetwork");
        networkStatus.disconnect();
        TestObserver<String> testObserver3 = new TestObserver<>();
        requestData(api)
                .compose(RetryTransformers.retryOnError(3, 100, fixNetwork(networkStatus, 0), NetworkException.class))
                .subscribe(testObserver3);
        testObserver3.awaitTerminalEvent();
        testObserver3.assertValue(result);
        System.out.println("case3 : retry succeed\n\n");

        System.out.println("start case4 : retry,delay 100ms,fixNetwork,limit total retry time 80ms");
        networkStatus.disconnect();
        TestObserver<String> testObserver4 = new TestObserver<>();
        requestData(api)
                .compose(RetryTransformers.retryOnError(3, new FixedBackOffStrategy(100), Observable.timer(80, TimeUnit.MILLISECONDS), fixNetwork(networkStatus, 0), null, NetworkException.class))
                .subscribe(testObserver4);
        testObserver4.awaitTerminalEvent();
        testObserver4.assertError(NetworkException.class);
        System.out.println("case4 : retry failed\n\n");

        System.out.println("start case5 : retry,delay 100ms,fixNetwork,limit total retry time 110ms");
        networkStatus.disconnect();
        TestObserver<String> testObserver5 = new TestObserver<>();
        requestData(api)
                .compose(RetryTransformers.retryOnError(3, new FixedBackOffStrategy(100), Observable.timer(110, TimeUnit.MILLISECONDS), fixNetwork(networkStatus, 0), null, NetworkException.class))
                .subscribe(testObserver5);
        testObserver5.awaitTerminalEvent();
        testObserver5.assertValue(result);
        System.out.println("case5 : retry succeed\n\n");
    }

    class User implements Validator {
        String username;
        String passwd;

        @Override
        public void validate() throws BaseException {
            if (username == null) {
                throw new DataException("user name null");
            }
        }
    }

    class NullableResult<T> implements Validator, DataConverter<NullableData<T>> {

        T data;

        @Override
        public NullableData<T> convert() {
            return NullableData.of(data);
        }

        @Override
        public void validate() throws BaseException {
            //skip
        }
    }

    @org.junit.Test
    public void testValidateNullable() {
        User user = new User();
        NullableResult<User> result = new NullableResult();
        result.data = user;

        TestObserver testObserver = new TestObserver();

        Observable.just(result)
                .compose(ValidateTransformers.validate())
                .compose(DataTransformers.convertToData())
                .compose(ValidateTransformers.validateNullable())
                .subscribe(testObserver);

        testObserver.assertError(DataException.class);

        user.username = "zhangsan";

        TestObserver testObserver2 = new TestObserver();

        Observable.just(result)
                .compose(ValidateTransformers.validate())
                .compose(DataTransformers.convertToData())
                .compose(ValidateTransformers.validateNullable())
                .subscribe(testObserver2);

        testObserver2.assertValueCount(1);

        result.data = null;

        TestObserver testObserver3 = new TestObserver();

        Observable.just(result)
                .compose(ValidateTransformers.validate())
                .compose(DataTransformers.convertToData())
                .compose(ValidateTransformers.validateNullable())
                .subscribe(testObserver3);

        testObserver3.assertValueCount(1);
        testObserver3.assertValue(new Predicate() {
            @Override
            public boolean test(Object o) throws Exception {
                return o instanceof NullableData && ((NullableData<User>) o).isNull();
            }
        });
    }
}
