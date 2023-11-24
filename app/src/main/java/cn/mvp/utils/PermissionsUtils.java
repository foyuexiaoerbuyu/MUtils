package cn.mvp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import cn.mvp.mlibs.utils.UIUtils;

public class PermissionsUtils {
    public static void requestReadWriteSdCard(Activity activity) {
        XXPermissions.with(activity).permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    UIUtils.tipToast("权限全部通过");
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    UIUtils.tipToast("部分权限未通过" + denied);
                    Log.i("调试信息", "noPermission:  " + denied);
                }
            }
        });

    }

    public static void requestDefPermissions(Activity activity) {
        XXPermissions.with(activity).permission(Manifest.permission.WRITE_SETTINGS).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    UIUtils.tipToast("部分权限未通过 " + denied);
                    Log.i("调试信息", "noPermission:  " + denied);
                }
            }
        });
    }

    /**
     * Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE
     * 判断是否缺少权限
     */
    public static boolean hasReadWriteSdCardPermission(Context activity) {
        return ContextCompat.checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(activity, Permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
    }

    /**
     * Permission.WRITE_EXTERNAL_STORAGE
     * 判断是否缺少权限
     */
    public static boolean hasReadWriteSdCardPermission(Context activity, String perm) {
        return ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_DENIED;
    }

    /**
     * 修改系统设置
     *
     * @return 是否有修改系统设置权限
     */
    public static boolean requestWriteSettingsPermissions(Context context, boolean requestPermission) {
        if (requestPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
            // 如果没有WRITE_SETTINGS权限，则发起权限请求
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context);
    }

    /*-------------------------------------------------------------------------------------*/
    public static boolean hasPermissions(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    /**
     * 如果返回true，表示之前已经请求过该权限，并且用户拒绝了授权。这时候可以向用户解释为何需要该权限，并再次请求权限。
     * 如果返回false，表示之前未请求过该权限、用户拒绝授权并勾选了"不再询问"选项，或者系统规定不再提示权限请求解释。这时候不应该再显示权限请求解释，直接向用户请求权限即可。
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }
        return false;
    }

    /**
     * 悬浮窗权限
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
     * <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
     */
    public static void openFloatingWindowPermission(Activity activity, int requestCode) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {//悬浮窗权限
//            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
//            activity.startActivityForResult(intent1, requestCode);
//        }
        if (checkFloatPermission(activity)) return;//有权限就不在处理
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            activity.startActivityForResult(intent, requestCode);
        } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);
        } else {//4.4-6.0以下
            //无需处理了
        }
    }

    /** 判断是否开启悬浮窗权限   context可以用你的Activity.或者this */
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    /**
     * 相机权限
     */
    public static void openCameraPermission(Activity activity, int requestCode) {
        // 检查相机权限是否已经被授予
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有被授予，就请求相机权限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
        }
    }
}
