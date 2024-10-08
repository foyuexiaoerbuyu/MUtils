package cn.mvp.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommUtils {
    private static int stepNumber = 0;

    /**
     * 匹配字符串中的url
     */
    public static String matchLink(String str, String def) {
        Pattern pattern = Pattern.compile("(?i)\\b((?:https?|ftp)://|www\\.)[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return def;
    }

    public static <T> void showListDialog(Context context, IClick<T> iClick, String... itemList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, Arrays.asList(itemList));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAdapter(adapter, (dialogInterface, position) -> {
            iClick.callBack(null, position);
        });

        builder.show();
    }

    public static void showCopyListDialog(Context context, List<String> itemList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, itemList);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setAdapter(adapter, (dialogInterface, position) -> {
            // 获取点击的项的文本
            String itemText = itemList.get(position);

            // 复制文本到剪贴板
            copyToClipboard(context, itemText);

            // 显示提示信息
            Toast.makeText(context, "已复制：" + itemText, Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            clipboard.setText(text);
        } catch (Exception e) {
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static String getText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return String.valueOf(clip.getItemAt(0).coerceToText(context));
        }
        return null;
    }

    public static void log(String msg) {
        if (msg.length() > 3000) {
            Log.d("调试信息", msg.substring(0, 3000));
            log(msg.substring(3000));
        } else {
            Log.d("调试信息", msg);
        }
    }

    public static void log(String tag, String msg) {
        if (msg.length() > 3000) {
            Log.d(tag, getScope() + msg.substring(0, 3000));
            log(tag, msg.substring(3000));
        } else {
            Log.d(tag, getScope() + msg);
        }
    }

    public static void getChildLog(String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String stackTraceMsgArr = stackTraceElement.toString();
        Log.i("调试信息", stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
    }

    public static void getChildLog(String tag, String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String stackTraceMsgArr = stackTraceElement.toString();
        Log.i(tag, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
    }

    private static String getScope() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        return "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")#" + trace.getMethodName() + "   ";
    }

    public static void showStepLogInfo() {
        Log.e("调试信息" + ":", getScope() + "  步骤   " + stepNumber++);
    }

    public static void showStepLogInfo(String msg) {
        Log.e("调试信息" + ":", getScope() + " log: " + msg);
    }

    /**
     * 请求权限
     * <!--读写文件-->
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <!-- Android11额外添加 -->
     * <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
     * tools:ignore="ScopedStorage" />
     * <p>
     * android:requestLegacyExternalStorage="true"
     */
    private void requestmanageexternalstorage_Permission(Activity context, int REQUEST_CODE, IPermissionCall iPermissionCall) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                iPermissionCall.callBack();
                Toast.makeText(context, "Android版本R或以上，有MANAGE_EXTERNAL_STORAGE授予!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Android版本R或以上，没有MANAGE_EXTERNAL_STORAGE授予!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    interface IPermissionCall {

        void callBack();
    }

    public interface IClick<T> {

        void callBack(T t, int pos);
    }

    private static long lastCallTime = 0;

    /**
     * 执行某个操作，并打印距离上次调用的耗时信息
     */
    public static void performOperation(String msg) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastCallTime;
        if (lastCallTime != 0) {
            Log.i("打印耗时", getScope() + "  " + msg + " 距离上次调用耗时：" + elapsedTime + " 毫秒" + "  当前时间：" + new SimpleDateFormat("yyyy-MM-dd kk:mm:ss:SSS", Locale.getDefault()).format(new Date()));
        }
        lastCallTime = System.currentTimeMillis();
    }

    /**
     * 执行某个操作，并打印距离上次调用的耗时信息
     */
    public static void performOperation() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastCallTime;
        if (lastCallTime != 0) {
            Log.i("打印耗时", getScope() + " 距离上次调用耗时：" + elapsedTime + " 毫秒" + "  当前时间：" + new SimpleDateFormat("yyyy-MM-dd kk:mm:ss:SSS", Locale.getDefault()).format(new Date()));
        }
        lastCallTime = System.currentTimeMillis();
    }

    /**
     * 格式化时间
     *
     * @param ms 毫秒值
     */
    public static String formatTime(int ms) {
        int s = ms / 1000; // 总秒数
        int m = s / 60; // 分钟数
        int ss = s % 60;// 除分钟数的秒数

        String res;
        if (m < 10)
            res = "0" + m + ":";
        else
            res = m + ":";
        if (ss < 10)
            res += ("0" + ss);
        else
            res += ss;

        return res;
    }

    // 发送本地广播
    public static void sendLocalBroadcast(Context context, String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // 发送本地广播，携带数据
    public static void sendLocalBroadcast(Context context, String action, String key, String value) {
        Intent intent = new Intent(action);
        intent.putExtra(key, value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // 发送本地广播，携带数据
    public static void sendLocalBroadcast(Context context, String action, Map<String, String> args) {
        Intent intent = new Intent(action);
        if (args != null) {
            for (Map.Entry<String, String> entry : args.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // 注册广播接收器
    public static void registerReceiver(Context context, BroadcastReceiver receiver, String action) {
        IntentFilter filter = new IntentFilter(action);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    // 注册广播接收器，可以监听多个 action
    public static void registerReceiver(Context context, BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    // 注销广播接收器
    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
    }
}
