package cn.mvp.mlibs.utils;


import android.os.Build;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 系统版本信息类
 * 如：版本>=2.2、获取系统Android版本、获得设备的固件版本号、判断是否是三星、中兴的手机
 * 获取CPU的信息、是否支持闪光灯或相机
 */
public class DeviceUtils {

//    /** Android版本>=2.2 SDK:8 @return 是否Android版本>=2.2 */
//    public static boolean is2_2() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO; }
//
//    /** Android版本>=2.3 SDK:9 @return 是否Android版本>=2.3 */
//    public static boolean is2_3() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD; }
//
//    /** Android版本>=3.0 SDK:11 @return 是否Android版本>=3.0 */
//    public static boolean is3_0() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB; }
//
//    /** Android版本>=3.1 SDK:12 @return 是否Android版本>=3.1 */
//    public static boolean is3_1() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1; }
//
//    /** Android版本>=4.0 SDK:14 @return 是否Android版本>=4.0 */
//    public static boolean is4_0() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH; }
//
//    /** Android版本>= 4.1 SDK:16 @return 是否Android版本>= 4.1 */
//    public static boolean is4_1() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN; }

    /**
     * Android版本>= 4.2
     * SDK:17
     *
     * @return 是否Android版本>= 4.2
     */
    public static boolean is4_2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * Android版本>=4.3
     * SDK:18
     *
     * @return 是否Android版本>=4.3
     */
    public static boolean is4_3() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /** Android版本>=4.4 SDK:19 @return 是否Android版本>=4.4 */
    public static boolean is4_4() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /** Android版本>=5.0 SDK:21 @return 是否Android版本>=5.0 */
    public static boolean is5_0() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /** Android版本>=6.0	  SDK:23 @return 是否Android版本>=6.0 */
    public static boolean is6_0() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /** Android版本>=7.0	  SDK:24 @return 是否Android版本>=7.0 */
    public static boolean is7_0() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /** Android版本>=7.1	  SDK:25 @return 是否Android版本>=7.1 */
    public static boolean is7_1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    }

    /** Android版本>=8.0.0 SDK:26 @return 是否Android版本>=8.0.0 */
    public static boolean is8_0() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /** Android版本>=8.1.0 SDK:27 @return 是否Android版本>=8.1.0 */
    public static boolean is8_1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;
    }

    /** Android版本>=9	  SDK:28 @return 是否Android版本>=9 */
    public static boolean is9_0() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /** Android版本>=10	  SDK:29 @return 是否Android版本>=10 Android10 */
    public static boolean is10_0() {
        return Build.VERSION.SDK_INT >= 29;
    }

    /**
     * 获取系统Android版本
     *
     * @return 系统Android版本
     */
    public static int getSDKVersionInt() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 获得设备的固件版本号
     *
     * @return 设备的固件版本号
     */
    public static String getReleaseVersion() {
        return (Build.VERSION.RELEASE == null) ? "" : Build.VERSION.RELEASE;
    }


    /**
     * 检测是否是中兴机器
     *
     * @return 是否是中兴机器
     */
    public static boolean isZte() {
        return getDeviceModel().toLowerCase().contains("zte");
    }

    /**
     * 判断是否是三星的手机
     *
     * @return 是否是三星的手机
     */
    public static boolean isSamsung() {
        return getManufacturer().toLowerCase().contains("samsung");
    }

    /**
     * 检测是否HTC手机
     *
     * @return 是否HTC手机
     */
    public static boolean isHTC() {
        return getManufacturer().toLowerCase().contains("htc");
    }

    /**
     * 检测当前设备是否是特定的设备
     *
     * @param devices 设备列表
     * @return 是否是特定的设备
     */
    public static boolean isDevice(String... devices) {
        String model = DeviceUtils.getDeviceModel();
        if (devices != null && !model.equals("")) {
            for (String device : devices) {
                if (model.contains(device)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获得设备型号
     *
     * @return 设备型号
     */
    public static String getDeviceModel() {
        Log.i("getDeviceModel", (Build.MODEL) == null ? "" : (Build.MODEL).trim());
        return (Build.MODEL) == null ? "" : (Build.MODEL).trim();
    }

    /**
     * 获取厂商信息
     *
     * @return 获取厂商信息
     */
    public static String getManufacturer() {
        return (Build.MANUFACTURER) == null ? "" : (Build.MANUFACTURER).trim();
    }


    /**
     * 判断是否是平板电脑
     *
     * @return 是否是平板电脑
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 获取CPU的信息
     *
     * @return CPU的信息
     */
    public static String getCpuInfo() {
        String cpuInfo = "";
        try {
            if (new File("/proc/cpuinfo").exists()) {
                FileReader fr = new FileReader("/proc/cpuinfo");
                BufferedReader localBufferedReader = new BufferedReader(fr,
                        8192);
                cpuInfo = localBufferedReader.readLine();
                localBufferedReader.close();
                if (cpuInfo != null) {
                    cpuInfo = cpuInfo.split(":")[1].trim().split(" ")[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuInfo;
    }

    /**
     * 判断是否支持闪光灯
     *
     * @return 是否支持闪光灯
     */
    public static boolean isSupportCameraLedFlash(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null
                            && PackageManager.FEATURE_CAMERA_FLASH
                            .equals(f.name)) // 判断设备是否支持闪光灯
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测设备是否支持相机
     *
     * @param context 上下文
     * @return 是否支持相机
     */
    public static boolean isSupportCameraHardware(Context context) {
        return context != null
                && context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }
}
