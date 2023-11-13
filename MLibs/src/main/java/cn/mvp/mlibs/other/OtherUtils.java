package cn.mvp.mlibs.other;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import cn.mvp.mlibs.utils.UIUtils;

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

    /**
     * 创建快捷方式
     *
     * @param shortcutName 名称
     * @param shortcutId   快捷方式id
     * @param icon         图标
     * @param targetActy   目标Activity
     */
    public static void createShortcut(String shortcutName, String shortcutId, int icon, Class<? extends Activity> targetActy) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent shortcutIntent = new Intent(UIUtils.getContext(), targetActy);
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(UIUtils.getContext(), icon));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            UIUtils.getContext().sendBroadcast(addIntent);
        } else {
            ShortcutManager shortcutManager = UIUtils.getContext().getSystemService(ShortcutManager.class);
            if (shortcutManager.isRequestPinShortcutSupported()) {
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(UIUtils.getContext(), shortcutId)
                                .setIcon(Icon.createWithResource(UIUtils.getContext(), icon))
                                .setShortLabel(shortcutName)
                                .setIntent(new Intent(UIUtils.getContext(), targetActy).setAction(Intent.ACTION_MAIN))
                                .build();

                Intent pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo);
                PendingIntent successCallback = PendingIntent.getBroadcast(UIUtils.getContext(), 0,
                        pinnedShortcutCallbackIntent, 0);
                shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
            } else {
                Log.i("调试信息", "createShortcut: 不支持创建快捷方式 ");
                Toast.makeText(UIUtils.getContext(), "不支持创建快捷方式", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getPullAdb(String path) {
        return "adb pull " + path + " \"D:/tmp/\"";
    }
}
