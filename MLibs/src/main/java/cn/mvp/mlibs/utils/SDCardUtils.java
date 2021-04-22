package cn.mvp.mlibs.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.mvp.mlibs.log.LogUtils;

import static android.os.Environment.getExternalStorageState;
import static cn.mvp.mlibs.utils.UIUtils.getResources;

/**
 * SD卡工具箱
 * 内部存储;外部存储;SD卡;扩展卡(可拔插存储卡)
 * https://blog.csdn.net/csdn_aiyang/article/details/80665185
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

    /** 读取Raw文件 R.raw.文件名   只能读不能写 */
    public static String readRaw(int id) {
        //有汉字用字符流来读
        StringBuilder sbd = new StringBuilder();
        BufferedReader reader = null;
        InputStream is = null;
        is = getResources().openRawResource(id);
        reader = new BufferedReader(new InputStreamReader(is));
        String row = "";
        try {
            while ((row = reader.readLine()) != null) {
                sbd.append(row);
                sbd.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sbd.toString();
    }

    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param id          资源ID R.raw.文件名
     * @param fileName    文件名
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        readInputStream(storagePath + File.separator + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param path 文件夹路径
     * @return 剩余控空间
     */
    public long getFreeSpace(String path) {
        if (FileUtils.isFolderExist(path)) {
            File file = new File(path);
            return file.getFreeSpace();
        }
        return 0;
    }

    /** 清除本应用内部缓存(/data/data/com.xxx.xxx/files) */
    public static void cleanInternalAllFiles(Context context) {
        deleteFile(context.getFilesDir());
    }

    /**
     * 删除文件(会删除文件夹)
     */
    private static boolean deleteFile(File dir) {
        if (dir == null || !dir.exists()) {
            return false;
        }
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File child : children) {
                boolean success = deleteFile(child);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}