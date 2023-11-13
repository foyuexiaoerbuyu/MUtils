package cn.mvp.mlibs.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cn.mvp.mlibs.MLibs;

public class BuildConfigUtil {
    public static boolean isDebug() {
        try {
            PackageManager pm = MLibs.getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(MLibs.getContext().getPackageName(), 0);
            return (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}