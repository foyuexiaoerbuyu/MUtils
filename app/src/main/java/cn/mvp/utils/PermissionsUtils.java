package cn.mvp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import android.util.Log;

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
    public static boolean hasReadWriteSdCardPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(activity, Permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
    }

}
