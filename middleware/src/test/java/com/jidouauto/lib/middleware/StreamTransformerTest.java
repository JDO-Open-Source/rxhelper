package com.jidouauto.lib.middleware;

import com.jidouauto.lib.middleware.exception.DataException;
import com.jidouauto.lib.middleware.exception.IdentityException;
import com.jidouauto.lib.middleware.transformer.StreamTransformer;

import org.junit.Test;

import java.net.ConnectException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.ReplaySubject;

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
    public class TestResult implements DataSource<String>, IdentityValidator, ResultValidator {
        /**
         * The M token.
         */
        int mToken;

        /**
         * Instantiates a new Test result.
         *
         * @param token the token
         */
        public TestResult(int token) {
            System.out.println("TestResult.TestResult");
            mToken = token;
        }

        @Override
        public String getData() {
            return "TEST";
        }

        @Override
        public void validateResult() throws DataException {
            System.out.println("TestResult.validateData");
        }

        @Override
        public void validateIdentity() throws IdentityException {
            if (mToken == 9) {
                System.out.println("TestResult.validateIdentity token 错误");
                throw new IdentityException(9, "token 错误");
            }
        }
    }

    /**
     * The Error.
     */
//模拟第一次从服务端刷新token失败，但第二次正常的逻辑
    boolean error = true;

    /**
     * Refresh token observable.
     *
     * @param newToken the new token
     * @return the observable
     */
    public Observable<Integer> refreshToken(int newToken) {
        return Observable.just(newToken)
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
     * Test transformer.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testTransformer() throws InterruptedException {
        //验证登录服务器token出错，并且尝试刷新token，同时刷新token时第一次出错第二次正常的情况
        TestObserver<String> cto = new TestObserver<>();
        Observable.just("A B C","E F","G H T")
                .compose(StreamTransformer.applyIOUI())
                .flatMap(str->Observable.fromArray(str.split(" ")))
                .subscribe(s-> System.out.print(s));

        ReplaySubject<String> replaySubject = ReplaySubject.create();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onComplete();
            }
        }).subscribe(replaySubject);

        replaySubject.subscribe(s-> System.out.println(s));
        replaySubject.subscribe(s-> System.out.println(s));
        replaySubject.getValue();
        replaySubject.cleanupBuffer();
        Thread.sleep(1000);
        replaySubject.subscribe(s-> System.out.println(s));

//        TestObserver<String> testObserver = new TestObserver<>();
//        Observable.defer((Callable<ObservableSource<TestResult>>) () -> Observable.just(new TestResult(getToken())))
//                .compose(StreamTransformer.validateIdentity())   //验证身份错误
//                .compose(StreamTransformer.validateResult())       //验证数据正确性
//                .compose(StreamTransformer.convertToData())           //数据转换
//                .compose(StreamTransformer.retryWhenError(IdentityException.class, 3, 0, refreshToken(9)))
//                .compose(StreamTransformer.retryExceptError(3, 0, IdentityException.class))
//                .compose(StreamTransformer.convertError())
//                .subscribe(testObserver);
//        testObserver.assertValue("TEST");
    }
}
