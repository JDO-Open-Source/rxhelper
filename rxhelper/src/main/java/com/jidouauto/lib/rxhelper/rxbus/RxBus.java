package com.jidouauto.lib.rxhelper.rxbus;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    private static final String TAG = "RxBus";
    private static final boolean DEBUG = true;

    private static RxBus sInstance;

    private ConcurrentHashMap<Object, List<Subject>> mSubjectsMapper = new ConcurrentHashMap<>();

    public static synchronized RxBus instance() {
        if (sInstance == null) {
            sInstance = new RxBus();
        }
        return sInstance;
    }

    private RxBus() {
    }

    public <T> Observable<T> register(Object tag, Class<T> clazz) {
        List<Subject> subjectList = mSubjectsMapper.get(tag);
        if (subjectList == null) {
            subjectList = new ArrayList<>();
            mSubjectsMapper.put(tag, subjectList);
        }

        // FIXME: Won't serialized the PublishSubject to SerializedSubject, thread unsafe
        Subject<T> subject = PublishSubject.create();
        subjectList.add(subject);
        if (DEBUG) {
            Log.d(TAG, "[register] mSubjectsMapper: " + mSubjectsMapper);
        }
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = mSubjectsMapper.get(tag);
        if (subjects != null) {
            subjects.remove(observable);
            if (subjects.isEmpty()) {
                mSubjectsMapper.remove(tag);
            }
            if (DEBUG) {
                Log.d(TAG, "[unregister] mSubjectsMapper: " + mSubjectsMapper);
            }
        }
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjects = mSubjectsMapper.get(tag);
        if (subjects != null && !subjects.isEmpty()) {
            for (Subject subject : subjects) {
                subject.onNext(content);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "[send] mSubjectsMapper: " + mSubjectsMapper);
        }
    }
}