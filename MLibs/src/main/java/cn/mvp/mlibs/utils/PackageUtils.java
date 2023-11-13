package cn.mvp.mlibs.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 包工具类
 */
public class PackageUtils {

    public static AllPackageInfos getAllPackageInfos(Context context) {
        PackageManager packageManager = context.getPackageManager();
        AllPackageInfos infos = new AllPackageInfos();
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : installedPackages) {
            AllPackageInfo aInfo = new AllPackageInfo();
            aInfo.setVersionName(info.versionName);
            aInfo.setVersionCode(info.versionCode);
            aInfo.setPackageName(info.packageName);
            aInfo.setFirstInstallTime(info.firstInstallTime);
            aInfo.setLastUpdateTime(info.lastUpdateTime);
            aInfo.setName(packageManager.getApplicationLabel(info.applicationInfo).toString());
            aInfo.setSystem((ApplicationInfo.FLAG_SYSTEM & info.applicationInfo.flags) != 0);//是否为系统文件
            infos.add(aInfo);
        }
        return infos;
    }


    public static void log(Context context) {
        List<AllPackageInfo> infos = getAllPackageInfos(context).getInfos();
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).isSystem()) {
                android.util.Log.i("调试信息", "系统应用: " + infos.get(i).getName() + " = " + infos.get(i).getPackageName());
            }
        }
        for (int i = 0; i < infos.size(); i++) {
            if (!infos.get(i).isSystem()) {
                android.util.Log.i("调试信息", "用户应用: " + infos.get(i).getName() + " = " + infos.get(i).getPackageName());
            }
        }

    }


    /**
     * 检测该包名所对应的应用是否存在
     *
     * @param packageName 包名
     * @return 指定应用是否已安装
     */
    public static boolean isInstall(Context context, String packageName) {

        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        if (packageName == null || TextUtils.isEmpty(packageName)) {
            return false;
        }

        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_GIDS);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(long time) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        ComponentName cn = intent.getComponent();
        String packageName = null;
        if (cn == null) {
            ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if ((info != null) && (info.activityInfo != null)) {
                packageName = info.activityInfo.packageName;
            }
        } else {
            packageName = cn.getPackageName();
        }
        if (packageName != null) {
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                return (info != null) && (info.applicationInfo != null) &&
                        ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static class AllPackageInfos {
        private List<AllPackageInfo> packageInfos;

        public AllPackageInfos() {
            this.packageInfos = new ArrayList<>();
        }

        public List<AllPackageInfo> getInfos() {
            return packageInfos;
        }

        public void add(AllPackageInfo allPackageInfo) {
            packageInfos.add(allPackageInfo);
        }
    }

    public static class AllPackageInfo {
        /**
         * 版本名称
         */
        private String versionName;
        /**
         * 应用名称
         */
        private String name;
        /**
         * 版本号
         */
        private int versionCode;
        /**
         * 包名
         */
        private String packageName;
        /**
         * 是否是系统应用
         */
        private boolean system;
        /**
         * 第一次安装时间
         */
        private String firstInstallTime;
        /**
         * 最后更新时间
         */
        private String lastUpdateTime;

        public boolean isSystem() {
            return system;
        }

        public void setSystem(boolean system) {
            this.system = system;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getFirstInstallTime() {
            return firstInstallTime;
        }

        public void setFirstInstallTime(long firstInstallTime) {
            this.firstInstallTime = stampToDate(firstInstallTime);
        }

        public String getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = stampToDate(lastUpdateTime);
        }
    }
}