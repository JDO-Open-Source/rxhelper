package com.jidouauto.lib.rxhelper;

import com.jidouauto.lib.rxhelper.ext.ObservableFirstSingle;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

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
        List<String> WORDS = Arrays.asList(new String[]{"A", "B", "C"});
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

    @Before
    public void setSchedulerBefore() {
        RxJavaPlugins.reset();
        RxJavaPlugins.setComputationSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setSingleSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.reset();
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Test
    public void testObservableSourceToPublisher() throws InterruptedException {
        ObservableSource<Long> os = Observable.interval(100, TimeUnit.MILLISECONDS);
        Single<Long> s = new ObservableFirstSingle<>(os);
        TestObserver<Long> testObserver = new TestObserver<>();
        s.subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValue(0l);
    }

    int i = 0;

    @Test
    public void testPublisherToObservable() throws InterruptedException {
        Flowable<Long> f = Flowable.interval(100, TimeUnit.MILLISECONDS)
                .map(l->{
                    System.out.println("ExampleUnitTest.testPublisherToObservable:"+l);
                    return l;
                });
        Observable<Long> s = Observable.fromPublisher(f);
        TestObserver<Integer> testObserver = new TestObserver<>();
        Observable.fromCallable(() -> 1 / i++)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                return s;
                            }
                        });
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("ExampleUnitTest.onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("ExampleUnitTest.onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("ExampleUnitTest.onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("ExampleUnitTest.onComplete");
                    }
                });
//        testObserver.assertSubscribed();
//        testObserver.assertValue(1);
//        testObserver.assertComplete();

        Thread.sleep(10_000);
    }
}