package cn.mvp.mlibs.other;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

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
            Log.e("调试信息", "closeDialogSafety: " + Log.getStackTraceString(e));
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
            Log.e("调试信息", "showDialogSafety: " + Log.getStackTraceString(e));
        }
    }

    /**
     * 杀死主进程
     */
    public static void killMainProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
