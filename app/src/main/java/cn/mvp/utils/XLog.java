package cn.mvp.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.mvp.BuildConfig;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.GsonUtils;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.utils.UIUtils;

/**
 * 错误日志打印工具，带定位功能（输出调用处类型名、方法名、行，点击可追踪到）
 * Created by HDL on 2017/5/4.
 * 有时间重新整理一下,增加所有日志写入本地文件控制
 */

public class XLog {
    private static final String APP_CRASH_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;
    /** 是否打印日志 */
    public static boolean isConsoleLog = true;
//    public static boolean isConsoleLog = BuildConfig.DEBUG;

    public static final String TEST_INFO = "";
    private static int stepNumber = 0;
    public static String TAG = "调试信息";
    private static String jaPrefix = "[";
    private static int logsegmentSize = 3072;//最长为4*1024设置为

    /**
     * 打印错误信息
     * 使用最新方法:XLog.showArgsInfo();
     *
     * @param msg 错误信息
     */
    @Deprecated
    public static void showLogInfo(Object msg) {
        if (isConsoleLog) {
            showLogs("", isEmpty(msg));
        }
    }


    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void tipShortInfo(String msg) {
        if (isConsoleLog && BuildConfig.DEBUG) {
            ToastUtils.show(StringUtil.defaultIfBlank(msg, "null"));
        }
    }

    public static void tipLongInfo(final Context context, final String msg) {
        if (isConsoleLog && BuildConfig.DEBUG) {
            XLog.getChildLog(msg);
            if (Looper.getMainLooper() == Looper.myLooper()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            } else {
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

        }
    }


    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void tipDialog(Context context, String msg) {
        if (isConsoleLog && BuildConfig.DEBUG) {
            UIUtils.tipToast(msg);
        }
    }

    /**
     * 普通日志输出
     *
     * @param localTag tag
     * @param logMsg   info
     */
    public static void commonLog(String localTag, String logMsg) {
        if (isConsoleLog) {
            if (logMsg.length() > logsegmentSize) {
                int count = logMsg.length() / logsegmentSize;
                for (int i = 0; i < count; i++) {
                    if (logMsg.length() > logsegmentSize) {
                        Log.v(localTag, logMsg.substring(0, logsegmentSize));
                        logMsg = logMsg.substring(logsegmentSize);
                    }
                }
            }
            Log.v(localTag, logMsg);
        }
    }

    /**
     * 打印错误信息
     */
    public static void showArgsInfo(Object... args) {
        if (isConsoleLog) {
            StringBuilder inputArgs = new StringBuilder();
            inputArgs.append("传入的数据:   ");
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i] == null ? "该参数为null" : args[i];
                inputArgs.append("参数").append(i).append(" = ").append(jaPrefix);
                if (arg instanceof String) {
                    inputArgs.append(isEmpty(arg));
                    if (inputArgs.length() > logsegmentSize) {
//                        int i1 = inputArgs.length() / logsegmentSize;
                        while (inputArgs.length() > logsegmentSize) {
                            showLogs("", inputArgs.substring(0, logsegmentSize));
                            inputArgs.replace(0, logsegmentSize, "");
                        }
                    }
                } else {
                    inputArgs.append(arg);
                }
                inputArgs.append("]");
                if (!(i == args.length - 1)) {
                    inputArgs.append(",");
                }
            }
            showLogs("", inputArgs.toString());
        }
    }

    public static String isEmpty(Object msg) {
        if (msg == null) {
            return "日志调用处为null";
        } else if (strNull.equals(msg)) {
            return "日志调用处为字符串null";
        } else if ("".equals(msg)) {
            return "日志调用处为空字符串\"\"";
        }
        return msg.toString();
    }

    /**
     * 打印步骤
     */
    public static void showStepLogInfo() {
        if (isConsoleLog) {
            showLogs("步骤", "" + stepNumber++);
        }
    }


    /**
     * 显示异常信息
     *
     * @param e 异常信息
     */
    public static void printExceptionInfo(Throwable e) {
        if (isConsoleLog) {
            Log.e(TAG, "抛异常了" + Log.getStackTraceString(e));
//            printMsgToFile(getStackTrace(), "输出异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 显示异常信息
     *
     * @param e 异常信息
     */
    public static String getExceptionInfo(Throwable e) {
        return Log.getStackTraceString(e);
    }


    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void file(String msg) {
        if (isConsoleLog) {
            printMsgToFile(getStackTrace(), "调试信息", msg);
        }
    }

    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void file(String fileName, String msg) {
        if (isConsoleLog) {
            printMsgToFile(getStackTrace(), fileName, msg);
        }
    }

    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void onlyWhiteFile(String fileName, String msg) {
        if (isConsoleLog) {
            printMsgToFile(null, fileName, msg);
        }
    }

    /**
     * 获取StackTraceElement对象-----当前调用XLog.showLogInfo()处的类信息（类名、方法名、行等）
     * 堆栈跟踪元素:当前调用Log.showLogInfo()处的类信息（类名、方法名、行等）
     *
     * @return .
     */
    private static StackTraceElement getStackTrace() {

        /*简化版:Thread.currentThread().getStackTrace()[4].toString().substring(23).replaceFirst("\\.", "#")*/
        return Thread.currentThread().getStackTrace()[5];
    }

    /**
     * 获取StackTraceElement对象-----当前调用XLog.showLogInfo()处的类信息（类名、方法名、行等）
     * 堆栈跟踪元素:当前调用Log.showLogInfo()处的类信息（类名、方法名、行等）
     *
     * @return .
     */
    private static String getStackTraceStr() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[5];
        return "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")" + "#" + trace.getMethodName() + "    ";
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param msg smg
     */
    public static void getChildLog(String msg) {
        if (isConsoleLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String stackTraceMsgArr = stackTraceElement.toString();
            Log.i(TAG, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
        }
    }

    /**
     * 打印日志
     *
     * @param msg .
     */
    private static void showLogs(String tag, String msg) {
        msg = msg.replace("(", "（").replace(")", "）");
        if (msg.length() > logsegmentSize) {
            while (msg.length() > logsegmentSize) {
                Log.i(TAG, getStackTraceStr() + tag + msg);
                msg = msg.substring(logsegmentSize);
            }
        }
        Log.i(TAG, getStackTraceStr() + tag + msg);
    }

    /**
     * 打印日志到文件
     *
     * @param element  当前调用XLog.showLogInfo()处的类信息（类名、方法名、行等）
     * @param fileName 文件名
     * @param msg      信息
     */
    private static void printMsgToFile(StackTraceElement element, String fileName, String msg) {
        StringBuilder sb = new StringBuilder();
        if (element != null) {
            sb.append(element.getMethodName()).append(" (").append(element.getFileName()).append(":").append(element.getLineNumber()).append(") ");
        }
        String start = "-----------------" + DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME) + "-------------------\n";
        String content = start + sb.toString() + "\n" + msg + "\n\n\n";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            boolean mkdirsSuccess = new File(APP_CRASH_PATH).mkdirs();
            if (mkdirsSuccess) {
                Log.e(TAG, "创建日志文件夹失败!!!");
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(APP_CRASH_PATH + fileName + ".txt", true);
                fos.write(content.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String joPrefix = "{";
    private static String joSuffix = "}";
    private static String strNull = "null";

    public static void printJson(String headString, String jsonStr) {
        String tag = headString;
        String s = "@";
        if (headString.contains(s)) {
            tag = headString.substring(0, headString.indexOf(s));
            headString = headString.substring(headString.indexOf(s) + 1);
        }
        if (isConsoleLog) {
            if (jsonStr == null) {
                showLogs("", "调用处为null");
            } else {
                if (strNull.equals(jsonStr)) {
                    showLogs("", "调用处为null字符串");
                } else {
                    String message;
                    try {

                        if (jsonStr.startsWith(joPrefix)) {
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
                            message = jsonObject.toString(4);
                        } else {
                            if (jsonStr.startsWith(jaPrefix)) {
                                JSONArray jsonArray = new JSONArray(jsonStr);
                                message = jsonArray.toString(4);
                            } else {
                                message = jsonStr;
                            }
                        }
                    } catch (JSONException e) {
                        message = jsonStr;
                    }

                    printLine(tag, true);
                    message = headString + System.getProperty("line.separator") + message;
                    String[] lines = message.split(System.getProperty("line.separator"));
                    for (String line : lines) {
                        if (line.length() <= logsegmentSize) {
                            Log.d(tag, "║ " + line);
                        } else {
                            while (line.length() > logsegmentSize) {
                                Log.d(tag, "║ " + line);
                                String msgs = line.substring(0, logsegmentSize);
                                line = line.replace(msgs, "");
                            }
                            Log.d(tag, "║ " + line);
                        }
                    }
                    printLine(tag, false);
                }
            }
        }
    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    public static void JsonObj(String tag, Object obj) {
        if (!isConsoleLog) {
            return;
        }
        String msg = GsonUtils.toJson(obj);
        msg = msg.replace("(", "（").replace(")", "）");
        if (msg.length() > logsegmentSize) {
            while (msg.length() > logsegmentSize) {
                Log.i(TAG, getStackTraceStr() + tag + msg);
                msg = msg.substring(logsegmentSize);
            }
        }
        Log.i(TAG, getStackTraceStr() + tag + msg);
    }
    public static void JsonObj(Object obj) {
        if (!isConsoleLog) {
            return;
        }
        String msg = GsonUtils.toJson(obj);
        msg = msg.replace("(", "（").replace(")", "）");
        if (msg.length() > logsegmentSize) {
            while (msg.length() > logsegmentSize) {
                Log.i(TAG, getStackTraceStr() + TAG + msg);
                msg = msg.substring(logsegmentSize);
            }
        }
        Log.i(TAG, getStackTraceStr() + TAG + msg);
    }

}
