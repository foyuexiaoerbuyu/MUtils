package cn.mvp.mlibs.utils;


import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 适配Android O
 */
public class NotificationUtil {

    private static int id = 0;
    private Context context;
    private NotificationManager notificationManager;

    private NotificationUtil() {
    }

    public static NotificationUtil getInstance() {
        return NotificationUtilsHolder.NotificationUtil;
    }

    /**
     * 初始化
     *
     * @param context 引用全局上下文
     */
    public void init(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * 创建通知通道
     *
     * @param channelId   通道id
     * @param channelName 通道名称
     * @param importance  通道级别  NotificationManager.IMPORTANCE_MIN: 静默;  NotificationManager.IMPORTANCE_HIGH:随系统使用声音或振动
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //是否绕过请勿打扰模式
        channel.canBypassDnd();
        //闪光灯
        channel.enableLights(true);
        //锁屏显示通知
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_SECRET);
        //闪关灯的灯光颜色
        channel.setLightColor(Color.RED);
        //桌面launcher的消息角标
        channel.canShowBadge();
        //是否允许震动
        channel.enableVibration(true);
        //获取系统通知响铃声音的配置
        channel.getAudioAttributes();
        //获取通知取到组
        channel.getGroup();
        //设置可绕过  请勿打扰模式
        channel.setBypassDnd(true);
        //设置震动模式
        channel.setVibrationPattern(new long[]{100, 100, 200});
        //描述
        channel.setDescription("描述");
        //设置角标
        channel.setShowBadge(true);
        //是否会有灯光
        channel.shouldShowLights();
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 发送通知
     *
     * @param channelId 通道id
     * @param title     标题
     * @param content   内容
     * @param intent    意图
     */
    public void sendNotification(String channelId, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                //用于支持 Android 7.1（API 25）及以下的设备
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(content)
                .setOnlyAlertOnce(true)
//                .setSmallIcon(R.drawable.ic_app_def)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notificationManager.notify(id++, builder.build());
    }


    /**
     * 检查通知是否开启
     */
    public boolean checkNotifyEnable() {
        NotificationManagerCompat notification = NotificationManagerCompat.from(context);
        return notification.areNotificationsEnabled();
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ///< 8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 通知权限申请
     *
     * @param context
     */
    public static void requestNotify(Context context) {
        /**
         * 跳到通知栏设置界面
         * @param context
         */
        Intent localIntent = new Intent();
        ///< 直接跳转到应用通知设置的代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            ///< 4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * 清除通知栏消息通知
     */
    public void clearNotification() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private static class NotificationUtilsHolder {
        public static final NotificationUtil NotificationUtil = new NotificationUtil();
    }
}