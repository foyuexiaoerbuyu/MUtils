package cn.mvp.mlibs.log;

import cn.mvp.mlibs.utils.StringUtil;

/**
 * 导入项目使用
 * 全局替换//import android.util.Log;
 * Created by efan on 2017/4/13.
 */

public class Log {

    /** 总开关 */
    public static boolean SWITCH = true;

    private static String DEFT_AG = "调试信息";
    private static int stepNumber = 0;
    private static int MaxLength = 1024 * 3;

    private static boolean V = SWITCH && true;
    private static boolean D = SWITCH && true;
    private static boolean I = SWITCH && true;
    private static boolean W = SWITCH && true;
    private static boolean E = SWITCH && true;

    public static void init(boolean isShowLog, String tag) {
        SWITCH = isShowLog;
        if (tag != null) {
            DEFT_AG = tag;
        }
    }

    public static void cv(String tag, String msg) {
        if (V) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.v(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.v(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void cd(String tag, String msg) {
        if (D) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void cd(String tag, String msg, Object... args) {
        if (D) {
            msg = String.format(msg, args);
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void ci(String tag, String msg) {
        if (I) {
            if (msg.length() > MaxLength) {
                for (int i = 0; i < msg.length() / MaxLength; i++) {
                    if (msg.length() > MaxLength) {
                        android.util.Log.i(tag, msg.substring(0, MaxLength));
                        msg = msg.substring(MaxLength);
                    }
                }
            }
            android.util.Log.i(tag, msg);
        }
    }

    public static void cw(String tag, String msg) {
        if (W) {
            if (msg.length() > MaxLength) {
                for (int i = 0; i < msg.length() / MaxLength; i++) {
                    if (msg.length() > MaxLength) {
                        android.util.Log.w(tag, msg.substring(0, MaxLength));
                        msg = msg.substring(MaxLength);
                    }
                }
            }
            android.util.Log.w(tag, msg);


        }
    }

    public static void ce(String tag, String msg) {
        if (E) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (V) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.v(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.v(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void d(String tag, String msg) {
        if (D) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void d(String tag, String msg, Object... args) {
        if (D) {
            msg = String.format(msg, args);
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.d(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void i(String tag, String msg) {
        if (I) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.i(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.i(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void w(String tag, String msg) {
        if (W) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.w(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.w(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void e(String tag, String msg) {
        if (E) {
            android.util.Log.e(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + msg);
        }
    }

    public static void v(String msg) {
        if (V) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.v(DEFT_AG, getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.v(DEFT_AG, getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void d(String msg) {
        if (D) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.d(DEFT_AG, getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.d(DEFT_AG, getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void i(String msg) {
        if (I) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.i(DEFT_AG, getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.i(DEFT_AG, getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void w(String msg) {
        if (W) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.w(DEFT_AG, getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.w(DEFT_AG, getScope() + "  " + msg);// 打印剩余日志
        }
    }

    public static void e(String msg) {
        if (E) {
            while (msg.length() > MaxLength) {// 循环分段打印日志
                String logContent = msg.substring(0, MaxLength);
                android.util.Log.e(DEFT_AG, getScope() + "  " + logContent);
                msg = msg.replace(logContent, "");
            }
            android.util.Log.e(DEFT_AG, getScope() + "  " + msg);// 打印剩余日志
        }
    }

    /**
     * 打印异常详细信息,推荐使用
     *
     * @param e e
     */
    public static void e(Exception e) {
        if (E) {
            android.util.Log.e(DEFT_AG + ":", getScope() + "  " + "", e);
        }
    }

    /**
     * 打印异常详细信息,推荐使用
     *
     * @param e e
     */
    public static void e(Throwable e) {
        if (E) {
            android.util.Log.e(DEFT_AG + ":", getScope() + "  " + "", e);
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

    private static final int DEFAULT_STACK_TRACE_LINE_COUNT = 4;

    private static String getScope() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        return "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")" + "#" + trace.getMethodName() + " ";

//        Thread.currentThread().getStackTrace()[4].toString().substring(23).replaceFirst("\\.", "#");

        // StackTraceElement trace = Thread.currentThread().getStackTrace()[DEFAULT_STACK_TRACE_LINE_COUNT];
        // android.util.Log.i(DEFT_AG, trace.getClassName());
        // String stackTraceMsgArr = trace.toString();
        // return stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + trace.getMethodName();
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param msg smg
     */
    public static void getChildLog(String msg) {
        if (V) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String stackTraceMsgArr = stackTraceElement.toString();
            android.util.Log.i(DEFT_AG, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
        }
    }


    /** 打印步骤 */
    public static void showStepLogInfo() {
        if (V) {
            android.util.Log.v("步骤: ", getScope() + "  " + "" + stepNumber++);
        }
    }


    public static String getStackTraceString(Exception e) {
        return android.util.Log.getStackTraceString(e);
    }

    public static void e(String tag, String s, Exception ex) {
        if (E) {
            android.util.Log.e(StringUtil.defaultIfBlank(tag, DEFT_AG), getScope() + "  " + s, ex);
        }
    }

    public static void e(String tag, String msg, Throwable e, Object... args) {
        if (E) {
            android.util.Log.e(StringUtil.defaultIfBlank(tag, DEFT_AG), String.format(msg, args), e);
        }
    }

    private int last;

    private String randomKey() {
        int random = (int) (10 * Math.random());
        if (random == last) {
            random = (random + 1) % 10;
        }
        last = random;
        return String.valueOf(random);
    }
}
