package cn.mvp.mlibs.log;


import android.util.Log;

import java.io.FileOutputStream;
import java.util.Arrays;

import cn.mvp.mlibs.MLibs;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.IOUtils;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.StringUtil;

/**
 * 不推荐使用,只不过获取位置方式比较简洁 后期删除
 * 缺点:只有v能打印完全日志
 * @author nesger.zhan
 */
@Deprecated
public class LogUtils {

    /** 总开关 */
    private static boolean SWITCH = true;

    private static String DEFT_AG = "调试信息";
    private static int stepNumber = 0;
    private static int MaxLength = 1024 * 3;

    private final static boolean V = SWITCH && true;
    private final static boolean D = SWITCH && true;
    private final static boolean I = SWITCH && true;
    private final static boolean W = SWITCH && true;
    private final static boolean E = SWITCH && true;

    public static void init(boolean isEnable, String tag) {
        SWITCH = isEnable;
        if (!StringUtil.isEmpty(tag)) {
            DEFT_AG = tag;
        }
    }

    public static void v(String tag, String msg) {
        if (V) {
            if (msg.length() > MaxLength) {
                for (int i = 0; i < msg.length() / MaxLength; i++) {
                    if (msg.length() > MaxLength) {
                        android.util.Log.v(tag + ":" + getScope(), msg.substring(0, MaxLength));
                        msg = msg.substring(MaxLength);
                    }
                }
            }
            android.util.Log.v(tag + ":" + getScope(), msg);
        }
    }

    public static void d(String tag, String msg) {
        if (D) {
            android.util.Log.d(tag + ":" + getScope(), msg);
        }
    }

    public static void i(String tag, String msg) {
        if (I) {
            android.util.Log.i(tag + ":" + getScope(), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (W) {
            android.util.Log.w(tag + ":" + getScope(), msg);
        }
    }

    public static void e(String tag, String msg) {
        if (E) {
            android.util.Log.e(tag + ":" + getScope(), msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (E) {
            android.util.Log.e(tag + ":" + getScope(), msg, tr);
        }
    }

    public static void v(String msg) {
        if (V) {
            android.util.Log.v(DEFT_AG + ":" + getScope(), msg);
        }
    }

    public static void d(String msg) {
        if (D) {
            android.util.Log.d(DEFT_AG + ":" + getScope(), msg);
        }
    }

    public static void i(String msg) {
        if (I) {
            android.util.Log.i(DEFT_AG + ":" + getScope(), msg);
        }
    }

    /**
     * 打印错误信息
     */
    public static void i(Object... args) {
        if (I) {
            StringBuilder inputArgs = new StringBuilder();
            inputArgs.append("传入的数据:   ");
            String msg = Arrays.toString(args);
            android.util.Log.i(DEFT_AG + ":" + getScope(), msg.substring(1, msg.length() - 1));
        }
    }

    public static void w(String msg) {
        if (W) {
            android.util.Log.w(DEFT_AG + ":" + getScope(), msg);
        }
    }

    public static void e(String msg) {
        if (E) {
            android.util.Log.e(DEFT_AG + ":" + getScope(), msg);
        }
    }

    /**
     * 打印异常详细信息,推荐使用
     *
     * @param e e
     */
    public static void e(Exception e) {
        if (E) {
            android.util.Log.e(DEFT_AG + ":" + getScope(), "", e);
        }
    }

    private static final int DEFAULT_STACK_TRACE_LINE_COUNT = 4;

    private static String getScope() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[DEFAULT_STACK_TRACE_LINE_COUNT];
        return "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")" + "#" + trace.getMethodName() + "   ";
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param msg smg
     */
    public static void getChildLog(String msg) {
        if (I) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String stackTraceMsgArr = stackTraceElement.toString();
            android.util.Log.i(DEFT_AG, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
        }
    }


    /** 打印步骤 */
    public static void showStepLogInfo() {
        if (I) {
            android.util.Log.i("步骤" + ":" + getScope(), "" + stepNumber++);
        }
    }

    /**
     * 打印异常详细信息,推荐使用
     *
     * @param e e
     */
    public static void printExceptionInfo(Exception e) {
        if (E) {
            android.util.Log.e(DEFT_AG + ":", getScope() + "  " + "", e);
        }
    }

    public static void file(String logPath, String content) {
        if (!SDCardUtils.isAvailable() || !SDCardUtils.isHasPermission(MLibs.getContext())) {
            LogUtils.e("读写日志失败,可能没有权限或SD卡有问题");
            return;
        }
        if (!FileUtils.makeDirs(logPath)) {
            Log.e(DEFT_AG, "创建日志文件夹失败!!!");
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logPath, true);
            fos.write(content.getBytes());
        } catch (Exception e) {
            printExceptionInfo(e);
        } finally {
            IOUtils.close(fos);
        }
    }
}