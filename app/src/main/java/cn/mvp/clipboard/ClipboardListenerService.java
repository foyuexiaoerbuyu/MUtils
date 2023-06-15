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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import cn.mvp.R;
import cn.mvp.mlibs.utils.ClipboardUtils;

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
                Log.i("调试信息", "剪切板修改了:  " + text);
            }
        };
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("Send".equals(action)) {
                Log.i("调试信息", "onStartCommand:  点击了发送..."+ClipboardUtils.getText(this));
            }if ("START".equals(action)) {
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
     * @param channelId
     * @param channelName
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName){
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
}
