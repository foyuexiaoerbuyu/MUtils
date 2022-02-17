package cn.mvp.mlibs;

import android.content.Context;
import android.os.Handler;
import android.os.Process;

public class MLibs {
    private static Context mContext;
    private static Handler mHandler;
    private static int mainThreadId;
    private static boolean isDebug = true;


    public static void init(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler();
//        mainThread = Thread.currentThread();
        mainThreadId = Process.myTid();
    }

    /**
     *
     * @param debug 主模块的BuildConfig.DEBUG
     */
    public static void init(Context context, boolean debug) {
        init(context);
        isDebug = debug;
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

    public static boolean isDebug() {
        checkInit();
        return isDebug;
    }

    private static void checkInit() {
        if (mContext == null) {
            throw new NullPointerException("依赖库未进行初始化!!!");
        }
    }
}
