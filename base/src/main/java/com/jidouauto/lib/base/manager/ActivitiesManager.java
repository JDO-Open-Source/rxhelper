package com.jidouauto.lib.base.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * The type Activities manager.
 *
 * @author eddie Activity 管理类
 */
public class ActivitiesManager implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ActivitiesHelper";

    /**
     * The constant debug.
     */
    public static boolean debug = false;

    private static ActivitiesManager instance;

    private final LinkedList<WeakReference<Activity>> activities = new LinkedList<>();

    private WeakReference<Activity> lastResumeActivity;
    private WeakReference<Activity> lastStartActivity;

    private ActivitiesManager(Application application) {
        if (application == null) {
            throw new NullPointerException("application is NULL!");
        }
        application.registerActivityLifecycleCallbacks(this);
    }

    /**
     * Get activities manager.
     *
     * @return the activities manager
     */
    public static ActivitiesManager get() {
        if (instance == null) {
            throw new NullPointerException("NOT init!");
        }
        return instance;
    }

    /**
     * 在应用Application中调用此方法初始化
     *
     * @param application the application
     */
    public static void init(Application application) {
        if (instance == null) {
            synchronized (ActivitiesManager.class) {
                if (instance == null) {
                    instance = new ActivitiesManager(application);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (debug) {
            Log.d(TAG, "onActivityCreated:" + activity.getClass().getSimpleName());
        }
        Iterator<WeakReference<Activity>> it = activities.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> ref = it.next();
            if (ref.get() == null || activity == ref.get()) {
                it.remove();
            }
        }
        activities.addLast(new WeakReference<>(activity));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (debug) {
            Log.d(TAG, "onActivityStarted:" + activity.getClass().getSimpleName());
        }
        lastStartActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (debug) {
            Log.d(TAG, "onActivityResumed:" + activity.getClass().getSimpleName());
        }
        lastResumeActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (debug) {
            Log.d(TAG, "onActivityPaused:" + activity.getClass().getSimpleName());
        }
        lastStartActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (debug) {
            Log.d(TAG, "onActivityStopped:" + activity.getClass().getSimpleName());
        }
        lastResumeActivity = null;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (debug) {
            Log.d(TAG, "onActivityDestroyed:" + activity.getClass().getSimpleName());
        }
        Iterator<WeakReference<Activity>> it = activities.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> ref = it.next();
            if (ref.get() == null || activity == ref.get()) {
                it.remove();
            }
        }
    }

    /**
     * APP是否在前台
     *
     * @return app处于前台 ，且没有onStop.
     */
    public boolean isAppFrontOfUser() {
        return lastStartActivity != null;
    }

    /**
     * App是否处于前台并且用户可处理状态
     *
     * @return app处于前台且没有onPause. boolean
     */
    public boolean isAppActive() {
        return lastResumeActivity != null;
    }

    /**
     * 退出应用
     */
    public void finishAll() {
        for (int i = activities.size() - 1; i > 0; i++) {
            WeakReference<Activity> ref = activities.get(i);
            if (ref.get() != null) {
                ref.get().finish();
            }
        }
    }

    /**
     * 只保留当前activity,退出其它Activity
     *
     * @param activity the activity
     */
    public void finishOthers(Activity activity) {
        for (WeakReference<Activity> ref : activities) {
            if (ref.get() != null && activity != ref.get()) {
                ref.get().finish();
            }
        }
    }
}
