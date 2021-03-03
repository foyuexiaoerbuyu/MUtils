package cn.mvp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;

import androidx.annotation.Nullable;
import cn.mvp.global.MyApplication;
import cn.mvp.mlibs.log.Log;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.UIUtils;
import cn.mvp.other.ScreenShotContentObserver;

/**
 * 拍照截图监听服务
 */
public class PhotoCaptureService extends Service {

    private ScreenShotContentObserver screenShotContentObserver;
    /**
     * 启用备份
     */
    private boolean isEnableBackUp;

    public static void startService(Context context, boolean isEnableBackUp) {
        Intent intent = new Intent(context.getApplicationContext(), PhotoCaptureService.class);
        try {
            intent.putExtra("arg_isEnableBackUp", isEnableBackUp);
            context.getApplicationContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), PhotoCaptureService.class);
        try {
            context.getApplicationContext().stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        screenShotContentObserver = new ScreenShotContentObserver(new Handler(), this) {
            @Override
            protected void onScreenShot(String path, String fileName) {
                if (isEnableBackUp) {
                    try {
                        String destFilePath = getAvailableDirPath() + FileUtils.getFileName(path);
                        //在图库里隐藏
                        FileUtils.writeFile(destFilePath + ".nomedia", "");
                        //复制截图或拍照
                        FileUtils.copyFile(path, destFilePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Log.i("开始拍照截图监听服务>注册监听观察者");
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
    }

    /**
     * @return 获取可用的文件夹路径
     */
    private String getAvailableDirPath() {
        String path;
        if (SDCardUtils.isAvailable()) {
            SDCardUtils.getExternalFilesDir(UIUtils.getContext());
            if (SDCardUtils.getExternalPublicStorageDirectory() != null) {
                path = SDCardUtils.getExternalPublicStorageDirectory() + File.separator + "imageBack" + File.separator;
            } else {
                path = SDCardUtils.getExternalFilesDir(UIUtils.getContext()) + File.separator + "imageBack" + File.separator;
            }
        } else {
            path = SDCardUtils.getInternalFilesDir(UIUtils.getContext()) + File.separator + "imageBack" + File.separator;
        }
        FileUtils.makeDirs(path);
        Log.i("path = " + path);
        return path;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isEnableBackUp = intent.getBooleanExtra("arg_isEnableBackUp", true);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getContentResolver().unregisterContentObserver(screenShotContentObserver);
            Log.i("销毁拍照截图监听服务>解除监听观察者");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}