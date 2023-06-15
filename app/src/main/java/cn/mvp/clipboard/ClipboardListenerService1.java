package cn.mvp.clipboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cn.mvp.R;
import cn.mvp.mlibs.utils.ClipboardUtils;
//
//public class ClipboardListenerService1 extends Service {
//
//    private ClipboardManager clipboardManager;
//    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
//            @Override
//            public void onPrimaryClipChanged() {
//                // 剪切板内容发生变化时执行的操作
//                CharSequence text = ClipboardUtils.getText(ClipboardListenerService1.this);
//                Log.i("调试信息", "剪切板修改了:  " + text);
//            }
//        };
//        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
//
//        // 创建通知
//        Notification notification = createNotification();
//        // 设置 ID 为 0 , 就不显示已通知了 , 但是 oom_adj 值会变成后台进程 11
//        // 设置 ID 为 1 , 会在通知栏显示该前台服务
//        startForeground(1, notification);
//
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        clipboardManager.removePrimaryClipChangedListener(clipChangedListener);
//    }
//
//    private Notification createNotification() {
//        Intent stopIntent = new Intent(this, ClipboardListenerService1.class);
//        stopIntent.setAction("STOP");
//        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        String channelId = null;
//        // 8.0 以上需要特殊处理
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channelId = createNotificationChannel("kim.hsl", "ForegroundService");
//        } else {
//            channelId = "";
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.icon_img)
//                .setContentTitle("剪切板监听服务")
//                .setContentText("点击停止监听")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(stopPendingIntent)
//                .setAutoCancel(true);
//
//        return builder.build();
//    }
//
//    /**
//     * 创建通知通道
//     * @param channelId
//     * @param channelName
//     * @return
//     */
//    @RequiresApi(Build.VERSION_CODES.O)
//    private String createNotificationChannel(String channelId, String channelName){
//        NotificationChannel chan = new NotificationChannel(channelId,
//                channelName, NotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        service.createNotificationChannel(chan);
//        return channelId;
//    }
//}