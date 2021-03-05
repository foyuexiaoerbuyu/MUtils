package cn.mvp.mlibs.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.util.Locale;

import androidx.core.content.FileProvider;
import cn.mvp.mlibs.fileprovider.FileProvider7;

/**
 * 调用系统软件,打开各种文件
 * 示例:
 * OpenFileUtil.getInstance(FirstClsActivity.this,"com.XXX.XX.XXX.fileProvider").openFile(Constant.APP_CACHE_PATH + "测试文件.pdf");
 * 需要做兼容7.0及以上
 */
public class OpenFileUtil {
    /** 声明各种类型文件的dataType **/
//    private static final String DATA_TYPE_ALL = "*/*";//未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
//    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
//    private static final String DATA_TYPE_VIDEO = "video/*";
//    private static final String DATA_TYPE_AUDIO = "audio/*";
//    private static final String DATA_TYPE_HTML = "text/html";
//    private static final String DATA_TYPE_IMAGE = "image/*";
//    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
//    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
//    private static final String DATA_TYPE_WORD = "application/msword";
//    private static final String DATA_TYPE_CHM = "application/x-chm";
//    private static final String DATA_TYPE_TXT = "text/plain";
//    private static final String DATA_TYPE_PDF = "application/pdf";

    private static OpenFileUtil mOpenFileUtil = null;
    private static Context mContext;
    private static String authority;

    /*private OpenFileUtil(Context context, String authority) {
        mContext = context;
        OpenFileUtil.authority = authority;
    }*/
    private OpenFileUtil(Context context) {
        mContext = context;
    }

    /*public static OpenFileUtil getInstance(Context context, String authority) {
        synchronized (OpenFileUtil.class) {
            if (mOpenFileUtil == null) {
                mOpenFileUtil = new OpenFileUtil(context, authority);
            }
        }
        return mOpenFileUtil;
    }*/

    public static OpenFileUtil getInstance(Context context) {
        synchronized (OpenFileUtil.class) {
            if (mOpenFileUtil == null) {
                mOpenFileUtil = new OpenFileUtil(context);
            }
        }
        return mOpenFileUtil;
    }

    /**
     * 获取对应文件的Uri
     *
     * @param intent 相应的Intent
     * @param file   文件对象
     * @return .
     */
    public static Uri getUri(Context context, String authority, Intent intent, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(context, authority, file);
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public boolean openFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return false;
        Intent intent;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            intent = getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            intent = getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            intent = getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            intent = getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            intent = getPptFileIntent(filePath);
        } else if (end.equals("xls")) {
            intent = getExcelFileIntent(filePath);
        } else if (end.equals("doc")) {
            intent = getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            intent = getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            intent = getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            intent = getTextFileIntent(filePath);
        } else {
            intent = getAllIntent(filePath);
        }
        mContext.startActivity(intent);
        return true;
    }

    // Android获取一个用于打开APK文件的intent
    private Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        FileProvider7.setIntentDataAndType(mContext, intent, "*/*", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    private Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.android.package-archive", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    private Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        FileProvider7.setIntentDataAndType(mContext, intent, "video/*", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    private Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        FileProvider7.setIntentDataAndType(mContext, intent, "audio/*", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    private Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    private Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "image/*", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    private Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.ms-powerpoint", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    private Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.ms-excel", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    private Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/msword", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    private Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/x-chm", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    private Intent getTextFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "text/plain", new File(param), false);
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    private Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/pdf", new File(param), false);
        return intent;
    }
}