package cn.mvp.mlibs.other;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.ClipboardManager;
import android.util.Log;

import com.hjq.toast.ToastUtils;

import cn.mvp.mlibs.MLibs;
import cn.mvp.mlibs.utils.UIUtils;

/*
 * Receives broadcast commands and controls clipboard accordingly.
 * The broadcast receiver is active only as long as the application, or its service is active.
 *
# 写入内容到剪切板
adb shell am broadcast -a clipper.set -f 0x01000000 --es "text" "set content"
adb shell am broadcast -a clipper.set -f 0x01000000 --es "text" "测试信息"

# 获取剪切板内容
adb shell am broadcast -a clipper.get
*
https://github.com/majido/clipper
 */
public class ClipperReceiver extends BroadcastReceiver {
    private static String TAG = "调试信息";

    public static String ACTION_GET = "clipper.get";
    public static String ACTION_GET_SHORT = "get";
    public static String ACTION_SET = "clipper.set";
    public static String ACTION_SET_SHORT = "set";
    public static String EXTRA_TEXT = "text";
    private ICallBack iCallBack;

    public static boolean isActionGet(final String action) {
        return ACTION_GET.equals(action) || ACTION_GET_SHORT.equals(action);
    }

    public static boolean isActionSet(final String action) {
        return ACTION_SET.equals(action) || ACTION_SET_SHORT.equals(action);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ClipboardManager cb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        Log.i("调试信息", "接收到广播:  " + intent.getAction());
        if (isActionSet(intent.getAction())) {
            String text = intent.getStringExtra(EXTRA_TEXT);
            ToastUtils.show("已复制pc剪切板内容到手机");
            if (text != null) {
                cb.setText(text);
                setResultCode(Activity.RESULT_OK);
                setResultData("文本被复制到剪贴板.");
                if (iCallBack != null) {
                    iCallBack.callBack(text);
                }
            } else {
                setResultCode(Activity.RESULT_CANCELED);
                setResultData("没有提供文本. 使用 -e text \"要粘贴的文本\"");
            }
        } else if (isActionGet(intent.getAction())) {
            ToastUtils.show("已复制剪切板内容到pc");
            CharSequence clip = cb.getText();
            if (clip != null) {
                Log.d(TAG, String.format("剪贴板 text: %s", clip));
                setResultCode(Activity.RESULT_OK);
                setResultData(clip.toString());
            } else {
                setResultCode(Activity.RESULT_CANCELED);
                setResultData("");
            }
        }
    }

    /**
     * 发送广播
     */
    public void sendOrderedBroadcast(Context context, String content) {
        Intent intent = new Intent();
        intent.putExtra(ClipperReceiver.EXTRA_TEXT, content);
        intent.setAction(ClipperReceiver.ACTION_GET);
        intent.setAction(ClipperReceiver.ACTION_GET_SHORT);
        intent.setAction(ClipperReceiver.ACTION_SET);
        intent.setAction(ClipperReceiver.ACTION_SET_SHORT);
        context.sendOrderedBroadcast(intent, null);
    }

    /**
     * 注册广播
     */
    public void registerReceiver(Context context, ICallBack iCallBack) {
        this.iCallBack = iCallBack;
        IntentFilter filters = new IntentFilter();
        filters.addAction(ClipperReceiver.ACTION_GET);
        filters.addAction(ClipperReceiver.ACTION_GET_SHORT);
        filters.addAction(ClipperReceiver.ACTION_SET);
        filters.addAction(ClipperReceiver.ACTION_SET_SHORT);
        context.registerReceiver(this, filters);
    }

    /**
     * 剪切回调
     */
    public interface ICallBack {
        void callBack(String content);
    }
}