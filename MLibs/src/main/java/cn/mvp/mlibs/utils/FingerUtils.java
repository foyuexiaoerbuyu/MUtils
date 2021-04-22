package cn.mvp.mlibs.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.RequiresApi;

/**
 * <uses-permission android:name="android.permission.USE_BIOMETRIC" />
 * <uses-permission android:name="android.permission.USE_FINGERPRINT" />
 * https://www.jianshu.com/p/69fa281ef9dc
 * https://cloud.tencent.com/developer/article/1176648
 * 指纹识别工具类
 * <p>
 * https://github.com/haganWu/FingerprintLoginDemo
 * https://github.com/foyuexiaoerbuyu/FingerprintLoginDemo
 */
public class FingerUtils {

    private static FingerUtils singleton = null;
    private final FingerprintManager fingerprintManager;
    private final KeyguardManager keyguardManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private FingerUtils(Context context) {
        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static FingerUtils getInstance(Context context) {
        if (singleton == null) {
            synchronized (FingerUtils.class) {
                if (singleton == null) {
                    singleton = new FingerUtils(context);
                }
            }
        }
        return singleton;
    }

    /**
     * @return 判断android 版本，如果小于 6.0 支持不了指纹
     */
    public boolean isSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * ②检查手机硬件（有没有指纹感应区）
     * 判断 手机硬件（有没有指纹感应区）就是手机是否支持传感
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isHardFinger() {
        return fingerprintManager != null && fingerprintManager.isHardwareDetected();
    }

    /**
     * ③检查手机是否开启锁屏密码
     * 检查手机是否开启锁屏密码（如手机未开锁，涉及到一个优先级问题，先解锁 后使用）
     */

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean isWindowSafe() {
        return keyguardManager != null && keyguardManager.isKeyguardSecure();
    }

    /**
     * ④检查手机是否已录入指纹
     * 检查手机是否有录入指纹
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isHaveHandler() {
        return fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints();
    }

    /**
     * 创建指纹验证
     * 参数中最重要的就是 cancellationSignal和 callback，其他传null 和 0 就行，
     * cancellationsignal  是用来取消指纹验证的，而callback 可以回调 指纹验证失败次数 或者指纹验证成功、
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void authenticate(FingerprintManager.CryptoObject cryptoObject, CancellationSignal cancellationSignal,
                             int flag,
                             FingerprintManager.AuthenticationCallback authenticationCallback, Handler handler) {
        if (fingerprintManager != null) {
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, flag, authenticationCallback, handler);
        }
    }

    /**
     * 取消指纹验证  . 应该不会用上
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cannelFinger(CancellationSignal cancellationSignal) {
        cancellationSignal.cancel();

    }
}