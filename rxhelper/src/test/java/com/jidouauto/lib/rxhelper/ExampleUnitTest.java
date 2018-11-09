package com.jidouauto.lib.rxhelper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    /**
     * Addition is correct.
     */
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMaybe() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> emitter) throws Exception {
                emitter.onSuccess("1");
                emitter.onComplete();
            }
        })
                .subscribe(o -> {
                    System.out.println("ExampleUnitTest.testMaybe:" + o);
                }, e -> e.printStackTrace(), () -> System.out.println("ExampleUnitTest.testMaybe.success"));
    }



    @Test
    public void testUsingTestObserver() {
        List<String> WORDS = Arrays.asList(new String[]{"A","B","C"});
        TestObserver<String> observer = new TestObserver<>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d. %s", index, string));

        observable.subscribe(observer);

        observer.awaitTerminalEvent();

        observer.assertComplete();
        observer.assertNoErrors();
        observer.assertValueCount(3);
    }

    @Test
    public void testCreate() {
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        emitter.onError(new NullPointerException("s"));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        emitter.onNext(3);
                        emitter.onComplete();
                    }
                })
                .onErrorReturnItem(2)
                .subscribe(integer -> System.out.println(integer), e -> e.printStackTrace(), () -> System.out.println("complete!"));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}