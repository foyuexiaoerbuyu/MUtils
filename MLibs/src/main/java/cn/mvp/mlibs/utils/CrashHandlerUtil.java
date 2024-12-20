package cn.mvp.mlibs.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.mvp.mlibs.log.Log;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * public class AndroidUtilsApplication extends Application {
 * public void onCreate() {
 * super.onCreate();
 * //崩溃处理
 * CrashHandlerUtil crashHandlerUtil = CrashHandlerUtil.getInstance();
 * crashHandlerUtil.init(this);
 * crashHandlerUtil.setCrashTip("很抱歉，程序出现异常，即将退出！");
 * }
 * }
 * Created by Administrator
 * on 2016/5/19.
 * 微信公众号：吴小龙同学
 * 个人博客：http://wuxiaolong.me/
 */
public class CrashHandlerUtil implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "调试信息";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandlerUtil INSTANCE = new CrashHandlerUtil();
    //程序的Context对象
    private Context mContext;
    //保存路径
    private String mPath = Environment.getExternalStorageDirectory().getPath() + "/crash/";
    private OnCrashListener mOnCrashListener;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    //    //用于格式化日期,作为日志文件名的一部分
//    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private String crashTip = "很抱歉，程序出现异常，即将退出！";

    public String getCrashTip() {
        return crashTip;
    }

    public void setCrashTip(String crashTip) {
        this.crashTip = crashTip;
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandlerUtil() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     *
     * @return 单例
     */
    public static CrashHandlerUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param path 存储路径
     */
    public void init(Context context, String path) {
        mPath = path;
        init(context);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param path 存储路径
     * @param onCrashListener 回调
     */
    public void init(Context context, String path, OnCrashListener onCrashListener) {
        mOnCrashListener = onCrashListener;
        init(context, path);
    }

    public interface OnCrashListener {
        void onCrash(String crashInfo, Throwable e);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * @param thread 线程
     * @param ex     异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
                e.printStackTrace();
            }
            //退出程序
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable 异常
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                throwable.printStackTrace();
                Toast.makeText(mContext, getCrashTip(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(throwable);
        if (mOnCrashListener != null) {
            mOnCrashListener.onCrash(infos.toString(), throwable);
        }
        Log.e(throwable);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx 上下文
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldVal = field.get(null);
                if (fieldVal != null) {
                    infos.put(field.getName(), fieldVal.toString());
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex 异常
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
//            String time = formatter.format(new Date());
            String fileName = "crash-" + DateUtil.formatCurrentDate(DateUtil.REGEX_DATE) + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (!SDCardUtils.isHasPermission(mContext)) {
                    mPath = mContext.getFilesDir().getPath() + "/crash/";
                }
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(mPath + fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(mPath + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

}