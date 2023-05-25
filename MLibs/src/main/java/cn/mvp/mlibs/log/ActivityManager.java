package cn.mvp.mlibs.log;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

/**
 * Activity管理类<br/>
 * 需要在Application中调用registerActivityLifecycleCallbacks注册
 */
public class ActivityManager implements Application.ActivityLifecycleCallbacks {
    //Activity栈
    private Stack<Activity> activities = new Stack<>();
    private static ActivityManager activityManager;

    private ActivityManager() {
    }

    /**
     * 单例
     *
     * @return activityManager instance
     */
    public static ActivityManager getInstance() {
        if (activityManager == null) {
            synchronized (ActivityManager.class) {
                if (activityManager == null) {
                    activityManager = new ActivityManager();
                }
            }
        }

        return activityManager;
    }

    /**
     * 获取Activity任务栈
     *
     * @return activity stack
     */
    public Stack<Activity> getActivityStack() {
        return activities;
    }

    /**
     * Activity 入栈
     *
     * @param activity Activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * Activity出栈
     *
     * @param activity Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
        }
    }

    /**
     * 结束某Activity
     *
     * @param activity Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 获取当前Activity
     *
     * @return current activity
     */
    public Activity getCurrentActivity() {
        return activities.lastElement();
    }

    /**
     * 结束当前Activity
     */
    public void finishActivity() {
        finishActivity(activities.lastElement());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeActivity(activity);
    }
}
