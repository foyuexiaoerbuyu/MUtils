package cn.mvp.mlibs.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

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
////
//    /** Android版本>=3.0 SDK:11 @return 是否Android版本>=3.0 */
//    public static boolean is3_0() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB; }
//
//    /** Android版本>=3.1 SDK:12 @return 是否Android版本>=3.1 */
//    public static boolean is3_1() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1; }
//
//    /** Android版本>=4.0 SDK:14 @return 是否Android版本>=4.0 */
//    public static boolean is4_0() { return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH; }

    /** Android版本>= 4.1 SDK:16 @return 是否Android版本>= 4.1 */
    public static boolean is4_1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /** Android版本>= 4.2 SDK:17 @return 是否Android版本>= 4.2 */
    public static boolean is4_2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /** Android版本>=4.3 SDK:18 @return 是否Android版本>=4.3 */
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
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                        return true;
                    }
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


    /**
     * 判断设备是否为平板(建议增加平板白名单,一起判断)
     * 屏幕物理尺寸大于6,且屏幕布局大于3为平板
     */
    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        return screenInches >= 6.0 &&
                ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    private static String sID = null;

    private static final String DEVICE_ID = "device_id";

    /**
     * 非真正设备唯一id,每次清除卸载安装后id会变化;
     * android_id 在1.恢复出厂设置 2.root/恢复root 3.三清 4.刷机 5.系统更新 6.软件修改（一般是模拟器，xposed，root）这三种情况下可能会发生改变
     * https://blog.csdn.net/qq_36009027/article/details/99681482
     */
    public synchronized static String id(Context context) {

        if (sID == null) {
            File deviceFile = new File(context.getFilesDir(), DEVICE_ID);
            try {
                if (!deviceFile.exists()) {
                    writeInstallationFile(context, deviceFile);
                }
                sID = readInstallationFile(deviceFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    /**
     * @return android_id
     */
    private static String getAndroidId(Context context) {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
            return androidId;
        }
        return UUID.randomUUID().toString();
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(Context context, File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
//        String id = UUID.randomUUID().toString();
        String id = getDeviceId(context);
        out.write(id.getBytes());
        out.close();

    }

    /**
     * 设备标识组合id
     *
     * @return 设备标识组合id
     */
    private static String getDeviceId(Context context) {
        String m_szDevIDShort = "35" +
                (Build.BOARD.length() % 10) + //主板
                (Build.BRAND.length() % 10) +//系统定制商
                (Build.CPU_ABI.length() % 10) +//cpu指令集
                (Build.DEVICE.length() % 10) + //设备参数
                (Build.MANUFACTURER.length() % 10) +//硬件制造商
                (Build.MODEL.length() % 10) +//手机型号，比如xiaomi5，vivox21
                (Build.PRODUCT.length() % 10);//手机制造商，比如Huawei
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString(); // 反射获取硬件序列号
            if (serial.equals("unknown")) {
                throw new UnknownError("硬件序列号获取失败");
            }
            return new UUID(m_szDevIDShort.replaceAll(" ", "").hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
//            serial = "serial";
//            serial = Build.FINGERPRINT; //唯一识别码
            serial = getAndroidId(context);
//            if (serial == null || "".equals(serial.trim())) {
//                String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//                Random random = new Random();
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < 8; i++) {
//                    int number = random.nextInt(62);
//                    sb.append(str.charAt(number));
//                }
//                serial = sb.toString();
//            }
        }
        return new UUID(m_szDevIDShort.replaceAll(" ", "").hashCode(), serial.hashCode()).toString();
    }

//    public static final String BOARD = null;    //主板
//    public static final String BOOTLOADER = null;   //系统启动程序版本号
//    public static final String BRAND = null;  //系统定制商
//    public static final String CPU_ABI = null;  //cpu指令集
//    public static final String CPU_ABI2 = null; //cpu指令集2
//    public static final String DEVICE = null;  //设备参数
//    public static final String DISPLAY = null;  //显示屏参数
//    public static final String FINGERPRINT = null;  //唯一识别码
//    public static final String HARDWARE = null;  //硬件名称
//    public static final String HOST = null;
//    public static final String ID = null;  //修订版本列表
//    public static final String MANUFACTURER = null;  //硬件制造商
//    public static final String MODEL = null;  //手机型号，比如xiaomi5，vivox21
//    public static final String PRODUCT = null;  //手机制造商，比如Huawei
//    public static final String RADIO = null;  //无线电固件版本
//    public static final String SERIAL = null;  硬件序列号
//    public static final String[] SUPPORTED_32_BIT_ABIS = null;  //32位cpu指令集
//    public static final String[] SUPPORTED_64_BIT_ABIS = null;  //64位cpu指令集
//    public static final String[] SUPPORTED_ABIS = null;
//    public static final String TAGS = null;  //描述Build的标签
//    public static final long TIME = 0L;
//    public static final String TYPE = null;   //build的类型
//    public static final String UNKNOWN = "unknown";
//    public static final String USER = null;


}
