package cn.mvp.mlibs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.File;

import androidx.annotation.RequiresApi;
import cn.mvp.mlibs.fileprovider.FileProvider7;

/*
 InstallUtils.installProcess(FirstClsActivity.this, file, new InstallUtils.InstallUtilsPermissions() {
                        @Override
                        public void permissionsAwardedSituation(boolean state) {
                            XLog.showArgsInfo(state);
                            if (state) {
                                InstallUtils.installApk(FirstClsActivity.this, file);
                            } else {
                                //8.0没有授权安装未知应用,可以在这里提示给用户一个对话框
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    XLog.showArgsInfo();
//                                    InstallUtils.startInstallPermissionSettingActivity(FirstClsActivity.this);
                                    InstallUtils.installApk(FirstClsActivity.this, file);
                                }
                            }
                        }
                    });
 */

/**
 * 纯安装(安装7.0需要适配文件路径共享权限"file:///"这种,8.0需要适配apk未知应用安装权限)
 */
public class InstallUtils {

    //安装应用的流程
    public static void installProcess(Context context, File apk, InstallUtilsPermissions installUtilsPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
//            ((Activity)context).onRequestPermissionsResult(, , );
            if (!haveInstallPermission) {//没有权限
                installUtilsPermissions.permissionsAwardedSituation(haveInstallPermission, apk);
                /*DialogUtils.showDialog(this, "安装应用需要打开未知来源权限，请去设置中开启权限",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startInstallPermissionSettingActivity();
                                }
                            }
                        }, null);*/
                return;
            }
        }
        //有权限，开始安装应用程序
        installApk(context, apk, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startInstallPermissionSettingActivity(Context context, int requestCode) {
        //注意这个是8.0新API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }

    /** 安装应用 使用时需要适配8.0安装权限(已经适配7.0 uri问题) */
    public static void installApk(Context context, File apk, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        FileProvider7.setIntentDataAndType(context, intent, "application/vnd.android.package-archive", apk, false);
        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 通过浏览器下载
     *
     * @param httpUrlApk apk地址
     */
    public static void installAPKWithBrower(Context context, String httpUrlApk, int requestCode) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(httpUrlApk));
        viewIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivityForResult(viewIntent, requestCode);
    }

    public interface InstallUtilsPermissions {
        /**
         * 8.0安装授权情况
         */
        void permissionsAwardedSituation(boolean haveInstallPermission, File filePath);
    }
}
