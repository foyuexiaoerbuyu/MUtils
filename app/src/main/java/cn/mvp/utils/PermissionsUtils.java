package cn.mvp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

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
}
