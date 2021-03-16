package cn.mvp.mlibs.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

import androidx.core.content.ContextCompat;
import cn.mvp.mlibs.log.LogUtils;

import static android.os.Environment.getExternalStorageState;

/**
 * SD卡工具箱
 * 内部存储;外部存储;SD卡;扩展卡(可拔插存储卡)
 */
public class SDCardUtils {
    /**
     * 获取SD卡的状态
     */
    public static String getState() {
        return getExternalStorageState();
    }


    /**
     * SD卡是否可用
     *
     * @return 只有当SD卡已经安装并且准备好了才返回true
     */
    public static boolean isAvailable() {
        return getState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取内部的根目录
     *
     * @return /data/data/package/files  app内部路径
     */
    public static File getInternalFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取内部的根目录
     *
     * @return /data/data/package/cache  app内部路径
     */
    public static File getInternalCacheDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * @return /storage/sdcard0/Android/data/package/files
     */
    public static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    /**
     * Environment.DIRECTORY_ALARMS 	/storage/sdcard0/Android/data/package/files/Alarms
     * Environment.DIRECTORY_DCIM 	/storage/sdcard0/Android/data/package/files/DCIM
     * Environment.DIRECTORY_DOWNLOADS 	/storage/sdcard0/Android/data/package/files/Download
     * Environment.DIRECTORY_MOVIES 	/storage/sdcard0/Android/data/package/files/Movies
     * Environment.DIRECTORY_MUSIC 	/storage/sdcard0/Android/data/package/files/Music
     * Environment.DIRECTORY_NOTIFICATIONS 	/storage/sdcard0/Android/data/package/files/Notifications
     * Environment.DIRECTORY_PICTURES 	/storage/sdcard0/Android/data/package/files/Pictures
     * Environment.DIRECTORY_PODCASTS 	/storage/sdcard0/Android/data/package/files/Podcasts
     * Environment.DIRECTORY_RINGTONES 	/storage/sdcard0/Android/data/package/files/Ringtones
     *
     * @return /storage/sdcard0/Android/data/package/files
     */
    public static File getExternalFilesDir(Context context, String directoryType) {
        return context.getExternalFilesDir(directoryType);
    }


    /**
     * 获取SD卡的根目录
     *
     * @return /storage/sdcard0
     */
    public static File getExternalPublicStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * SD卡的九大公有目录
     * Environment.DIRECTORY_ALARMS 	/storage/sdcard0/Alarms
     * Environment.DIRECTORY_DCIM 	/storage/sdcard0/DCIM
     * Environment.DIRECTORY_DOWNLOADS 	/storage/sdcard0/Download
     * Environment.DIRECTORY_MOVIES 	/storage/sdcard0/Movies
     * Environment.DIRECTORY_MUSIC 	/storage/sdcard0/Music
     * Environment.DIRECTORY_NOTIFICATIONS 	/storage/sdcard0/Notifications
     * Environment.DIRECTORY_PICTURES 	/storage/sdcard0/Pictures
     * Environment.DIRECTORY_PODCASTS 	/storage/sdcard0/Podcasts
     * Environment.DIRECTORY_RINGTONES 	/storage/sdcard0/Ringtones
     */
    public static File getExternalStoragePublicDirectory(String directoryType) {
        return Environment.getExternalStoragePublicDirectory(directoryType);
    }

    /**
     * 注意：没有root的手机不能打开该文件夹的
     *
     * @return /data
     */
    public static File getSysDataDirectory() {
        return Environment.getDataDirectory();
    }

    /**
     * 注意：没有root的手机不能打开该文件夹的
     *
     * @return /cache
     */
    public static File getSysDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * 注意：没有root的手机不能打开该文件夹的
     *
     * @return /system
     */
    public static File getSysRootDirectory() {
        return Environment.getRootDirectory();
    }

    /**
     * 是否有读写权限
     *
     * @return 是否缺少某项权限
     */
    public static boolean isHasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * 删除文件或文件夹
     *
     * @param path 路径
     * @return .
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return file.delete();
            }
            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
        return file.delete();
    }


    /**
     * 扫描路径下的文件
     *
     * @param file 文件路径
     */
    public static void scanningFilesName(File file) {
        if (file == null) {
            return;
        }
        File[] fs = file.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory()) {    //若是目录，则递归打印该目录下的文件
                    LogUtils.d("文件夹:" + f.getPath());
                    scanningFilesName(f);
                }
                if (f.isFile())        //若是文件，直接打印
                    LogUtils.d("文件:" + f.getPath());
            }
        }
    }

}