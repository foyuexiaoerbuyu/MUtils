package cn.mvp.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommUtils {
    private static int stepNumber = 0;

    /** 匹配字符串中的url */
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
            Log.d(tag, msg.substring(0, 3000));
            log(msg.substring(3000));
        } else {
            Log.d(tag, msg);
        }
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
}
