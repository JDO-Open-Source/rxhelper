package com.jidouauto.lib.rxhelper.rxbus;

import org.junit.Assert;
import org.junit.Test;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;

public class RxBusTest {

    Disposable d;
    Disposable d2;
    Disposable d3;

    int i = 0;

    @Test
    public void testRxBus() {
        TestObserver<String> testObserver = new TestObserver<>();
        TestObserver<String> testObserver2 = new TestObserver<>();
        TestObserver<String> testObserver3 = new TestObserver<>();
        SimpleRxBus
                .getInstance()
                .observe(String.class)
                .doOnSubscribe(disposable -> d = disposable)
                .subscribe(testObserver);
        SimpleRxBus
                .getInstance()
                .observe(String.class)
                .map(s -> {
                    System.out.println("testRxBus() called : " + i);
                    if (i == 0) {
                        i++;
                        throw new NullPointerException();
                    }
                    return s;
                })
                .doOnSubscribe(disposable -> d2 = disposable)
                .subscribe(testObserver2);
        SimpleRxBus
                .getInstance()
                .post("1");
        testObserver.assertValue("1");
        testObserver2.assertError(NullPointerException.class);


        SimpleRxBus
                .getInstance()
                .observe(String.class)
                .doOnSubscribe(disposable -> d3 = disposable)
                .subscribe(testObserver3);
        SimpleRxBus
                .getInstance()
                .post("2");
        testObserver.assertValues("1", "2");
        testObserver2.assertError(NullPointerException.class);
        testObserver3.assertValue("2");

        d.dispose();
        d2.dispose();

        SimpleRxBus
                .getInstance()
                .post("3");
        testObserver.assertValues("1", "2");
        testObserver2.assertError(NullPointerException.class);
        testObserver3.assertValues("2", "3");

        Assert.assertTrue(d.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        Assert.assertFalse(d3.isDisposed());

        d3.dispose();

        TestObserver<String> testObserver4 = new TestObserver<>();
        SimpleRxBus.getInstance()
                .observe(String.class)
                .subscribe(testObserver4);
        SimpleRxBus
                .getInstance()
                .post("4");
        testObserver4.assertValues("4");
        testObserver3.assertValues("2", "3");
        testObserver2.assertError(NullPointerException.class);
        testObserver.assertValues("1", "2");

        TestObserver<String> testObserver5 = new TestObserver<>();
        SimpleRxBus.getInstance().observe("tag", String.class)
                .subscribe(testObserver5);
        SimpleRxBus.getInstance().post("tag", "5");
        testObserver5.assertValue("5");
        testObserver4.assertValues("4");
        testObserver3.assertValues("2", "3");
        testObserver2.assertError(NullPointerException.class);
        testObserver.assertValues("1", "2");
    }
}