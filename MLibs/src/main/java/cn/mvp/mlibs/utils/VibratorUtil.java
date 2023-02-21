package cn.mvp.mlibs.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * boolean hasVibrator ()//检查硬件是否有振动器
 * void vibrate (long milliseconds)//控制手机制动milliseconds毫秒
 * void vibrate (long[] pattern,  int repeat)//让手机以指定pattern模式震动。
 * void cancel ()//关闭震动
 * <p>
 * <uses-permission android:name="android.permission.VIBRATE" />
 * 震动工具类
 */
public class VibratorUtil {

    /**
     * 震动100毫秒只震动一次
     */
    public static void vibrate(final Context context) {
        long milliseconds = 100;
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 震动指定毫秒数，只震动一次
     * long milliseconds ：震动的时长，单位是毫秒
     *
     * @param milliseconds 震动指定毫秒数
     */
    public static void vibrate(final Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 自定义震动模式
     *
     * @param pattern  数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。 单位是毫秒
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void vibrate(final Context context, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);//-1为反复震动
    }

    /** 取消震动 */
    public static void virateCancle(final Context context) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }

}
