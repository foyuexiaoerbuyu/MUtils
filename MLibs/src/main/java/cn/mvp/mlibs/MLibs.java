package cn.mvp.mlibs;

import android.content.Context;
import android.os.Handler;
import android.os.Process;

public class MLibs {
    private static Context mContext;
    private static Handler mHandler;
    private static int mainThreadId;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler();
//        mainThread = Thread.currentThread();
        mainThreadId = Process.myTid();
    }

    public static Context getContext() {
        checkInit();
        return mContext;
    }

    public static Handler getHandler() {
        checkInit();
        return mHandler;
    }

    public static int getMainThreadId() {
        checkInit();
        return mainThreadId;
    }

    private static void checkInit() {
        if (mContext == null) {
            throw new NullPointerException("依赖库未进行初始化!!!");
        }
    }
}
