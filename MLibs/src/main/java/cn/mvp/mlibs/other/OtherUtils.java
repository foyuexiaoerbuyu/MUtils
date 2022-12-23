package cn.mvp.mlibs.other;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;

public class OtherUtils {

    /**
     * 安全关闭对话框 .
     * 避免报：not attached to window manager .
     */
    public static void closeDialogSafety(Activity activity, Dialog dialog, ICallBack iCallBack) {
        try {
            if (!activity.isFinishing() && dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                if (iCallBack != null) {
                    iCallBack.back();
                }
            } else {
                throw new CheckException("安全关闭弹框失败,请具体分析原因");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安全打开对话框 .
     * 避免报：not attached to window manager .
     */
    public static void showDialogSafety(Activity activity, Dialog dialog, ICallBack iCallBack) {
        try {
            if (!activity.isFinishing() && dialog != null && !dialog.isShowing()) {
                dialog.setCanceledOnTouchOutside(false);
                if (iCallBack != null) {
                    iCallBack.back();
                }
                dialog.show();
            } else {
                throw new CheckException("安全关闭弹框失败,请具体分析原因");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否使屏幕常亮
     * https://juejin.cn/post/6844903479727620103
     * https://www.cnblogs.com/sparrowlhl/p/11249004.html
     * 其他方式:
     *          View.keepScreenOn(true);
     *      　　mMediaPlayer.setScreenOnWhilePlaying(true);
     */
    public static void keepScreenLongLight(Activity activity, boolean isOpenLight) {
//        boolean isOpenLight = CommSharedUtil.getInstance(activity).getBoolean(CommSharedUtil.FLAG_IS_OPEN_LONG_LIGHT, true);
        Window window = activity.getWindow();
        if (isOpenLight) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

}
