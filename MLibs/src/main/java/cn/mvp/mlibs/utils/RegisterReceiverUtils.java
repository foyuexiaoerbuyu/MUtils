package cn.mvp.mlibs.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 广播注册解注工具
 * https://developer.aliyun.com/article/487526
 * @author Sahadev
 * 系统的LocalBroadcastManager好像也可以,待验证
 *
 */
public class RegisterReceiverUtils {
    private static Map<Class<?>, BroadcastReceiver> MAPS = new ArrayMap<Class<?>, BroadcastReceiver>();

    /**
     * RegisterReceiverUtils.registerBroadcastReceiver(mApplication, PttReceiver.class, "");
     *
     * @param context
     * @param broadcastReceiverClass
     * @param action
     */
    public static void registerBroadcastReceiver(Context context, Class<? extends BroadcastReceiver> broadcastReceiverClass, String action) {
        ArrayList<String> actions = new ArrayList<>();
        actions.add(action);
        registerBroadcastReceiver(context, broadcastReceiverClass, actions);
    }

    /**
     * RegisterReceiverUtils.registerBroadcastReceiver(mApplication, PttReceiver.class, "");
     *
     * @param context
     * @param broadcastReceiverClass
     */
    public static void registerBroadcastReceiver(Context context, Class<? extends BroadcastReceiver> broadcastReceiverClass, List<String> actions) {
        IntentFilter filter = new IntentFilter();
        for (int i = 0; i < actions.size(); i++) {
            filter.addAction(actions.get(i));
        }
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        try {

            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) broadcastReceiverClass.newInstance();
            if (broadcastReceiver != null) {
                MAPS.put(broadcastReceiverClass, broadcastReceiver);
                context.registerReceiver(broadcastReceiver, filter);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void unregisterBroadcastReceiver(Context context, Class<?> broadcastReceiverClass) {
        BroadcastReceiver broadcastReceiver = MAPS.get(broadcastReceiverClass);
        context.unregisterReceiver(broadcastReceiver);
        MAPS.remove(broadcastReceiverClass);

    }

}