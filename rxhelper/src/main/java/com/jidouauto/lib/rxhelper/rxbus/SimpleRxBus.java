package com.jidouauto.lib.rxhelper.rxbus;

import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author eddie
 */
public class SimpleRxBus {
    private static final String TAG = "RxBus";
    private static final boolean DEBUG = true;

    private static SimpleRxBus sInstance;

    private ConcurrentHashMap<Object, Subject<Object>> mSubjectsMapper = new ConcurrentHashMap<>();

    public static synchronized SimpleRxBus getInstance() {
        if (sInstance == null) {
            synchronized (SimpleRxBus.class) {
                if (sInstance == null) {
                    sInstance = new SimpleRxBus();
                }
            }
        }
        return sInstance;
    }

    private SimpleRxBus() {
    }

    public <T> Observable<T> observe(Class<T> clazz) {
        return observe(TAG, clazz);
    }

    public <T> Observable<T> observe(Object tag, Class<T> clazz) {
        Subject<Object> subject = mSubjectsMapper.get(tag);
        if (subject == null) {
            subject = PublishSubject
                    .create()
                    .toSerialized();
            mSubjectsMapper.put(tag, subject);
        }
        if (DEBUG) {
            System.out.println("[observe] mSubjectsMapper: " + mSubjectsMapper);
        }
        return subject.ofType(clazz);
    }

    public void removeTag(@NonNull Object tag) {
        mSubjectsMapper.remove(tag);
    }

    public void post(@NonNull Object content) {
        post(TAG, content);
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        Subject subject = mSubjectsMapper.get(tag);
        if (subject == null) {
            return;
        }
        subject.onNext(content);
        if (DEBUG) {
            System.out.println("[send] mSubjectsMapper: " + mSubjectsMapper);
        }
    }
}