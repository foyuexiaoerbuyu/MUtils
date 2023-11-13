package cn.mvp.mlibs.utils;


import android.content.Context;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author wlj
 * @date 2017/3/29
 * @email wanglijundev@gmail.com
 * @packagename wanglijun.vip.androidutils.utils
 * @desc: io操作
 */

public class MLbsApplication {


    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}