package cn.mvp.mlibs.log;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 错误日志打印工具，带定位功能（输出调用处类型名、方法名、行，点击可追踪到）
 * Created by HDL on 2017/5/4.
 */

public class XLog {

    private static int stepNumber = 0;

    /**
     * 是否打印日志
     */
    public static boolean isDebug = true;

    public static String TAG = "调试信息";
    private static String jaPrefix = "[";
    private static int logMaxLength = 3 * 1024;

    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void showLogInfo(Object msg) {
        if (isDebug) {
            String tipMsg = null;
            if (msg == null) {
                tipMsg = "日志调用处为null";
            } else if ("".equals(msg)) {
                tipMsg = "日志调用处为空字符串\"\"";
            } else if (strNull.equals(msg)) {
                tipMsg = "日志调用处为字符串\"null\"";
            }
            if (tipMsg != null) {
                showLogs("", tipMsg);
                return;
            }
            showLogs("", msg.toString());
        }
    }

    /**
     * 打印错误信息
     */
    public static void showArgsInfo(Object... args) {
        if (isDebug) {
            StringBuilder inputArgs = new StringBuilder();
            inputArgs.append("传入的数据:   ");
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i] == null ? "该参数为null" : args[i];
                inputArgs.append("参数").append(i).append(" = ").append(jaPrefix);
                if (arg instanceof String) {
                    if (arg.toString().length() == 0) {
                        arg = "该参数为空字符串\"\"";
                    }
                    if ("null".equals(arg)) {
                        arg = "该参数为\"null\"字符串";
                    }
                    inputArgs.append(arg);
                    if (inputArgs.length() > logMaxLength) {
                        int i1 = inputArgs.length() / logMaxLength;
                        while (inputArgs.length() > logMaxLength) {
                            showLogs("", inputArgs.substring(0, logMaxLength));
                            inputArgs.replace(0, logMaxLength, "");
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

    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void showMapLogInfo(Map msg) {

        if (isDebug) {
            if (msg != null) {
                switch (msg.toString()) {
                    case "null":
                        showLogs("方法参数", "日志调用处为字符串null");
                        break;
                    case "":
                        showLogs("方法参数", "日志调用处为空字符串\"\"");
                        break;
                    default:
                        showLogs("方法参数", "");
                        createBody(msg.toString());
                        break;
                }
            } else {
                showLogs("方法参数", "日志调用处为null");
            }
        }
    }

    private static void createBody(String inputArg) {

        if (inputArg.startsWith(joPrefix)) {
            inputArg = inputArg.substring(1);
        }
        if (inputArg.endsWith(joSuffix)) {
            inputArg = inputArg.substring(0, inputArg.length() - 1);
        }

        String string = "";
        String[] out = inputArg.split(",");
        for (String anOut : out) {
            String key = anOut.substring(0, anOut.indexOf("="));
            String value = anOut.substring(anOut.indexOf("=") + 1);
            String format = String.format("%s<%s>%s</%s>", string, key, value, key);
            Log.i(TAG, format);
        }
    }

    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void showLogInfo(String tag, Object msg) {

        if (isDebug) {
            if (msg != null) {
                if (strNull.equals(msg)) {
                    showLogs(tag, "日志调用处为字符串null");
                } else if ("".equals(msg)) {
                    showLogs(tag, "日志调用处为空字符串\"\"");
                } else {
                    showLogs(tag, msg.toString());
                }
            } else {
                showLogs(tag, "日志调用处为null");
            }
        }
    }

    /**
     * 打印步骤
     */
    public static void showStepLogInfo() {
        if (isDebug) {
            showLogs("步骤   ", "" + stepNumber++);
        }
    }


    /**
     * 显示异常信息
     *
     * @param e 异常信息
     */
    public static void printExceptionInfo(Throwable e) {
        if (isDebug) {
            Log.e(TAG, "抛异常了" + Log.getStackTraceString(e));
        }
    }


    /**
     * 打印错误信息
     *
     * @param msg 错误信息
     */
    public static void file(String fileName, String msg) {
        printMsgToFile(getStackTrace(), fileName, msg);
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
     * 调用处子类方法所在位置
     *
     * @param msg smg
     */
    static void getChildLog(String msg) {
        if (isDebug) {
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
        StackTraceElement element = getStackTrace();
        msg = msg.replace("(", "（").replace(")", "）");
        StringBuilder logInfo = new StringBuilder();
        String lTag = (TextUtils.isEmpty(tag) ? "" : (tag + ":"));
        logInfo.append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")").append("#").append(element.getMethodName()).append("  ").append(lTag);
        int segmentSize = 3 * 1024;
        if (msg.length() <= segmentSize) {
            Log.i(TAG, logInfo.toString() + msg);
        } else {
            while (msg.length() > segmentSize) {
                Log.i(TAG, logInfo.toString() + msg);
                String msgs = msg.substring(0, segmentSize);
                msg = msg.replace(msgs, "");
            }
            Log.i(TAG, logInfo.toString() + msg);
        }
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
        String className = element.getFileName();
        sb.append(element.getMethodName())
                .append(" (").append(className).append(":")
                .append(element.getLineNumber())
                .append(") ");
        String content = "-----------------------   " +
                SimpleDateFormat.getInstance().format(new Date(System.currentTimeMillis()))
                + "   -----------------------\n" + sb.toString() + "\n" +
                msg + "\n\n\n";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String errLogFilePath = Environment.getExternalStorageDirectory().getPath() + "/crash/";
            boolean mkdirsSuccess = new File(errLogFilePath).mkdirs();
            if (mkdirsSuccess) {
                Log.e(TAG, "创建日志文件夹失败!!!");
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(errLogFilePath + fileName + ".txt", true);
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
        if (isDebug) {
            if (jsonStr == null) {
                showLogs("调用处为null", "");
            } else {
                if (strNull.equals(jsonStr)) {
                    showLogs("调用处为null字符串", "");
                } else {
                    String message;
                    try {

                        if (jsonStr.startsWith(joPrefix)) {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonStr);
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
                    int segmentSize = 3 * 1024;
                    for (String line : lines) {
                        if (line.length() <= segmentSize) {
                            Log.d(tag, "║ " + line);
                        } else {
                            while (line.length() > segmentSize) {
                                Log.d(tag, "║ " + line);
                                String msgs = line.substring(0, segmentSize);
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

}
