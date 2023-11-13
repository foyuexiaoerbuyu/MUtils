package cn.mvp.mlibs.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/*
 * 通用广播
 */
public class MlibReceiver extends BroadcastReceiver {
    private static String TAG = "MlibReceiver";

    private IReceiverCallBack iCallBack;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (iCallBack == null) {
            Log.e(TAG, "onReceive: 回调为空");
            return;
        }
        iCallBack.callBack(context, intent);
    }

    /**
     * 发送广播
     * intent.putExtra(MlibReceiver.EXTRA_TEXT, content);
     * intent.setAction(MlibReceiver.ACTION_GET);
     */
    public void sendOrderedBroadcast(Context context, Intent intent, IReceiverCallBack iCallBack) {
        this.iCallBack = iCallBack;
        context.sendOrderedBroadcast(intent, null);
    }

    /**
     * 注册广播
     * filters.addAction(MlibReceiver.ACTION_GET);
     */
    public void registerReceiver(Context context, IntentFilter filters, IReceiverCallBack iCallBack) {
        this.iCallBack = iCallBack;
        context.registerReceiver(this, filters);
    }

    /**
     * 广播回调
     */
    public interface IReceiverCallBack {
        void callBack(Context context, Intent intent);
    }
}