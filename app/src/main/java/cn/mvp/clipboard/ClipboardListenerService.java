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
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;

import cn.mvp.R;
import cn.mvp.chat1.ClipboardWebSocketClient;
import cn.mvp.chat1.WebSocketChatMsg;
import cn.mvp.global.Constant;
import cn.mvp.mlibs.utils.ClipboardUtils;


/**
 * Android如何监控App使用剪切板权限的行为?
 * <p>
 * startService(new Intent(this, ClipboardListenerService.class).setAction("START"));
 * startService(new Intent(this, ClipboardListenerService.class).setAction("STOP"));
 * // 启动剪贴板监控服务
 * Intent serviceIntent = new Intent(this, ClipboardListenerService.class);
 * startService(serviceIntent);
 * <p>
 * https://blog.csdn.net/u010231454/article/details/131457953
 */
public class ClipboardListenerService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private ClipboardManager clipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;

    @Override
    public void onCreate() {
        super.onCreate();

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                // 处理剪切板变化的逻辑
                CharSequence text = ClipboardUtils.getText(ClipboardListenerService.this);
                Log.i("调试信息", "剪切板进行了修改:  " + text);
            }
        };
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
        new Thread(() -> ClipboardWebSocketClient.getInstance().connService(Constant.WS_URL + DeviceUtils.getManufacturer() + "_" + DeviceUtils.getModel(),
                new ClipboardWebSocketClient.IReceiver() {
                    @Override
                    public void onReceiverMsg(String msg) {
                    }

                    @Override
                    public void onErr(Exception e) {
                        Log.e("调试信息", "onErr:  ", e);
                    }

                    @Override
                    public void log(String log) {
                        LogUtils.i("  log = " + log);
                    }

                    @Override
                    public void progress(String msg, int currPrs, WebSocketChatMsg fileInfo) {

                    }
                })).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("Send".equals(action)) {
                Log.i("调试信息", "onStartCommand:  点击了发送..." + ClipboardUtils.getText(this));
            }
            if ("START".equals(action)) {
                startForeground(NOTIFICATION_ID, createNotification());
            } else if ("STOP".equals(action)) {
                clipboardManager.removePrimaryClipChangedListener(clipChangedListener);
                stopForeground(true);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    private Notification createNotification() {
        PendingIntent sendPendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, ClipboardListenerService.class).setAction("Send"),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent startPendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, ClipboardListenerService.class).setAction("START"),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, ClipboardListenerService.class).setAction("STOP"),
                PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = null;
        // 8.0 以上需要特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("kim.hsl", "ForegroundService");
        } else {
            channelId = "";
        }
        return new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Clipboard Listener")
                .setContentText("Listening to clipboard changes")
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,
                        "Send", sendPendingIntent).build())
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,
                        "Start", startPendingIntent).build())
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground,
                        "Stop", stopPendingIntent).build())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .build();
    }


    /**
     * 创建通知通道
     *
     * @param channelId
     * @param channelName
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        clipboardManager.removePrimaryClipChangedListener(clipChangedListener);
    }
}
