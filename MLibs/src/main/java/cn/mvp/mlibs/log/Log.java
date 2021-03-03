package cn.mvp.mlibs.log;

/**
 * 导入项目使用
 * 全局替换//import android.util.Log;
 * Created by efan on 2017/4/13.
 */

public class Log {

    /** 总开关 */
    public final static boolean SWITCH = true;

    /**
     * 启用默认tag(自定义tag实效)
     */
    public final static boolean IS_ENABLE_DEF_TAG = true;

    private final static String DEFT_AG = "调试信息";
    private static int stepNumber = 0;
    private static int MaxLength = 1024 * 3;

    private final static boolean V = SWITCH && true;
    private final static boolean D = SWITCH && true;
    private final static boolean I = SWITCH && true;
    private final static boolean W = SWITCH && true;
    private final static boolean E = SWITCH && true;

    public static void v(String tag, String msg) {
        if (V) {
            if (msg.length() > MaxLength) {
                for (int i = 0; i < msg.length() / MaxLength; i++) {
                    if (msg.length() > MaxLength) {
                        android.util.Log.v(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + msg.substring(0, MaxLength));
                        msg = msg.substring(MaxLength);
                    }
                }
            }
            android.util.Log.v(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (D) {
            android.util.Log.d(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + msg);
        }
    }

    public static void d(String tag, String msg, Object... args) {
        if (D) {
            android.util.Log.d(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, String.format(msg, args));
        }
    }

    public static void i(String tag, String msg) {
        if (I) {
            android.util.Log.i(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (W) {
            android.util.Log.w(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (E) {
            android.util.Log.e(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + msg);
        }
    }

    public static void v(String msg) {
        if (V) {
            android.util.Log.v(DEFT_AG + ":", getScope() + "  " + msg);
        }
    }

    public static void d(String msg) {
        if (D) {
            android.util.Log.d(DEFT_AG + ":", getScope() + "  " + msg);
        }
    }

    public static void i(String msg) {
        if (I) {
            android.util.Log.i(DEFT_AG + ":", getScope() + "  " + msg);
        }
    }

    public static void w(String msg) {
        if (W) {
            android.util.Log.w(DEFT_AG + ":", getScope() + "  " + msg);
        }
    }

    public static void e(String msg) {
        if (E) {
            android.util.Log.e(DEFT_AG + ":", getScope() + "  " + msg);
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
        return "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")" + "#" + trace.getMethodName() + "    ";

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
            android.util.Log.v("步骤" + ":", getScope() + "  " + "" + stepNumber++);
        }
    }


    public static String getStackTraceString(Exception e) {
        return android.util.Log.getStackTraceString(e);
    }

    public static void e(String tag, String s, Exception ex) {
        if (E) {
            android.util.Log.e(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, getScope() + "  " + s, ex);
        }
    }

    public static void e(String tag, String msg, Throwable e, Object... args) {
        if (E) {
            android.util.Log.e(IS_ENABLE_DEF_TAG ? DEFT_AG : tag, String.format(msg, args), e);
        }
    }

}
