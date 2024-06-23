package cn.mvp.other;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class ComUtils {

    /**
     * 请求权限
     * <!--读写文件-->
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
     * <p>
     * android:requestLegacyExternalStorage="true"
     */
    public static void requestRead2writePermission(Activity context, int requestCode) {
        //运行设备>=Android 11.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //检查是否已经有权限
            if (!Environment.isExternalStorageManager()) {
                //跳转新页面申请权限
                context.startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), requestCode);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(context, permissions, requestCode);
        }
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * @return 字符串或空字符串
     */
    public static String str(Object str) {
        return str == null ? "" : str.toString();
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param msg smg
     */
    public static void getChildLog(String TAG, String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String stackTraceMsgArr = stackTraceElement.toString();
        Log.i(TAG, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
    }

}
