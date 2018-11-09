package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.transformer.StreamTransformer;

import org.junit.Assert;
import org.junit.Before;

import java.net.ConnectException;
import java.util.concurrent.Future;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * The type Stream transformer test.
 */
public class StreamTransformerTest {

    /**
     * The Code.
     */
    int code = 9;

    /**
     * The type Test result.
     */
    public class Test implements DataConverter<String>, Validator<BaseException> {
        /**
         * The M token.
         */
        int mToken;

        /**
         * Instantiates a new Test result.
         *
         * @param token the token
         */
        public Test(int token) {
            System.out.println("Test.Test");
            mToken = token;
        }

        @Override
        public String convert() {
            return "TEST";
        }

        @Override
        public void validate() throws BaseException {
            System.out.println("Test.validateData");
            if (mToken == 9) {
                System.out.println("Test.validateIdentity token 错误");
                throw new IdentityException("token 错误");
            }
        }
    }

    /**
     * The Error.
     */
//模拟第一次从服务端刷新token失败，但第二次正常的逻辑
    boolean error = false;

    /**
     * Refresh token observable.
     *
     * @param newToken the new token
     * @return the observable
     */
    public Single<Integer> refreshToken(int newToken) {
        return Single.just(newToken)
                .map(i -> {
                    if (error) {
                        error = false;
                        System.out.println("StreamTransformerTest.refreshToken:net error" + newToken);
                        throw new ConnectException("can not connect server!");
                    } else {
                        System.out.println("StreamTransformerTest.refreshToken:ok");
                        return i;
                    }
                })
                .map(i -> code = newToken)
                .compose(StreamTransformer.retryAnyError(3, 0));

    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public int getToken() {
        System.out.println("StreamTransformerTest.getToken:" + code);
        return code;
    }

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

    /**
     * Test transformer.
     *
     * @throws InterruptedException the interrupted exception
     */
    @org.junit.Test
    public void testTransformer() throws Exception {
        //验证登录服务器token出错，并且尝试刷新token，同时刷新token时第一次出错第二次正常的情况
        TestObserver<String> testObserver = new TestObserver<>();
        Observable.fromCallable(() -> new Test(getToken()))
                .compose(StreamTransformer.validate())       //验证数据正确性
                .compose(StreamTransformer.convertToData())           //数据转换
                .compose(StreamTransformer.retryWhenError(IdentityException.class, 3, 200, refreshToken(1)))
                .compose(StreamTransformer.retryExceptError(3, 0, IdentityException.class))
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValue("TEST");
//        Assert.assertEquals("TEST", result.get());
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
                .compose(StreamTransformer.validate())
                .compose(StreamTransformer.convertToData())
                .compose(StreamTransformer.validateNullable())
                .subscribe(testObserver);

        testObserver.assertError(DataException.class);

        user.username = "zhangsan";

        TestObserver testObserver2 = new TestObserver();

        Observable.just(result)
                .compose(StreamTransformer.validate())
                .compose(StreamTransformer.convertToData())
                .compose(StreamTransformer.validateNullable())
                .subscribe(testObserver2);

        testObserver2.assertValueCount(1);

        result.data = null;

        TestObserver testObserver3 = new TestObserver();

        Observable.just(result)
                .compose(StreamTransformer.validate())
                .compose(StreamTransformer.convertToData())
                .compose(StreamTransformer.validateNullable())
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
