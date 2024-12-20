/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.mvp.mlibs.log;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_ACCELEROMETER;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cn.mvp.mlibs.MLibs;
import cn.mvp.mlibs.other.ICallBack;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.GsonUtil;
import cn.mvp.mlibs.utils.IOUtils;
import cn.mvp.mlibs.utils.SDCardUtils;
import cn.mvp.mlibs.utils.VibratorUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 日志相关类:默认是测试环境<br>
 * <b>支持：存储Log日志文件到本地。发送Log日志信息到服务器</b>
 *
 * @author yuchao.wang
 * @since 2014-4-23
 */
public class XLogUtil {
    private static boolean isShowLog = true;
    private static String TAG = "调试信息";

    /*==================新加常用================*/

    private static int stepNumber = 0;

    public static void init(boolean isShowLog, String TAG) {
        XLogUtil.isShowLog = isShowLog;
        if (TAG != null) {
            XLogUtil.TAG = TAG;
        }
    }

    public static void registerActivityLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(ActivityManager.getInstance());
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void showActivity(Application application, boolean isShowLog) {
        if (!isShowLog) return;
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i("当前所在", activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /* ========================下面的是本地存储相关的========================== */
    /**
     * 写日志对象
     */
    private LogWriter logWriter;

    /**
     * 写入本地日志线程
     */
    private class LogWriter extends Thread {
        /**
         * 文件路径
         */
        private String mFilePath;
        /**
         * 调用这个类的线程
         */
        private int mPid;
        /**
         * 线程运行标志
         */
        private boolean isRunning = true;

        /**
         * @param filePath 文件路径
         * @param pid
         */
        public LogWriter(String filePath, int pid) {
            this.mPid = pid;
            this.mFilePath = filePath;
        }

        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);//日期格式化对象
            Process process = null;//进程
            BufferedReader reader = null;
            FileWriter writer = null;
            try {
                //执行命令行
                String cmd = "logcat *:e *:w | grep";
                process = Runtime.getRuntime().exec(cmd);
                //得到输入流
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
                //创建文件
                File file = new File(mFilePath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                writer = new FileWriter(file, true);
                //循环写入文件
                String line = null;
                while (isRunning) {
                    line = reader.readLine();
                    if (line != null && line.length() > 0) {
                        writer.append("PID:" + this.mPid + "\t"
                                + sdf.format(new Date(System.currentTimeMillis())) + "\t" + line
                                + "\n");
                        writer.flush();
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                process = null;
                reader = null;
                writer = null;
            }
        }

        public void end() {
            isRunning = false;
        }
    }

    /**
     * 整个应用只需要调用一次即可:开始本地记录
     *
     * @param filePath 要写入的目的文件路径
     * @param iswrite  是否需要写入sdk
     */
    public void startWriteLogToSdcard(String filePath, boolean iswrite) {

        if (iswrite) {
            if (logWriter == null) {
                try {
                    /** LogUtil这个类的pid,必须在类外面得到 */
                    logWriter = new LogWriter(filePath, android.os.Process.myPid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logWriter.start();
        }
    }

    /**
     * 整个应用只需要调用一次即可:结束本地记录
     */
    public void endWriteLogToSdcard() {
        if (logWriter != null) {
            logWriter.end();
        }
    }

    /* ========================下面的是直接使用的========================== */

    /**
     * verbose详细日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void v(String tag, String message) {
        if (isShowLog) {
            android.util.Log.v(tag + ":", getScope() + "  " + message);
        }
    }

    /**
     * verbose详细日志
     *
     * @param message 日志信息
     */
    public static void v(String message) {
        if (isShowLog) {
            android.util.Log.v(TAG + ":", getScope() + "  " + message);
        }
    }

    /**
     * error错误日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void e(String tag, String message) {
        if (isShowLog) {
            android.util.Log.e(tag + ":", getScope() + "  " + message);
        }
    }

    /**
     * error错误日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void e(String tag, String message, Throwable e) {
        if (isShowLog) {
            android.util.Log.e(tag + ":", getScope() + "  " + message, e);
        }
    }

    /**
     * error错误日志
     *
     * @param message 日志信息
     */
    public static void e(String message) {
        if (isShowLog) {
            android.util.Log.e(TAG + ":", getScope() + "  " + message);
        }
    }

    /**
     * info信息日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void i(String tag, String message) {

        if (isShowLog) {
            android.util.Log.i(tag + ":", getScope() + "  " + message);
        }
    }

    /**
     * info信息日志
     *
     * @param message 日志信息
     */
    public static void i(String message) {

        if (isShowLog) {
            android.util.Log.e(TAG + ":", getScope() + "  " + message);
        }
    }

    /**
     * debug调试日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void d(String tag, String message) {

        if (isShowLog) {
            android.util.Log.d(tag + ":", getScope() + "  " + message);
        }
    }

    /**
     * debug调试日志
     *
     * @param message 日志信息
     */
    public static void d(String message) {

        if (isShowLog) {
            android.util.Log.d(TAG + ":", getScope() + "  " + message);
        }
    }

    /**
     * warn警告日志
     *
     * @param tag     日志标记
     * @param message 日志信息
     */
    public static void w(String tag, String message) {

        if (isShowLog) {
            android.util.Log.w(tag + ":", getScope() + "  " + message);
        }
    }

    /**
     * warn警告日志
     *
     * @param message 日志信息
     */
    public static void w(String message) {

        if (isShowLog) {
            android.util.Log.w(TAG + ":", getScope() + "  " + message);
        }
    }

    /**
     * 得到默认tag【类名】以及信息详情
     *
     * @param message 要显示的信息
     * @return 默认tag【类名】以及信息详情,默认信息详情【类名+方法名+行号+message】
     */
//    private static void getTagAndDetailMessage(String tag, String level, String message) {
//        String output[] = new String[2];
//        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
//        if (tag == null) {
//            output[0] = ste.getFileName();
//            tag = output[0];
//            output[1] = "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + "#" + ste.getMethodName();
//        } else {
//            output[1] = "";
//        }
//
//        int logMaxSize = 3 * 1024;
//        if (message == null) {
//            showLogInfo(tag, "e", output[1], "调用处为null");
//            return;
//        } else if (message.equals("null")) {
//            showLogInfo(tag, "e", output[1], "调用处为null字符串: \"null\"");
//            return;
//        } else if (message.equals("")) {
//            showLogInfo(tag, "e", output[1], "调用处为空字符串: \"\"");
//            return;
//        }
//        if (message.length() <= logMaxSize) {
//            showLogInfo(tag, level, output[1], message);
//        } else {
//            if (!"json".equals(level)) {
//                while (message.length() > logMaxSize) {
//                    showLogInfo(tag, level, output[1], message);
//                    message = message.replace(message.substring(0, logMaxSize), "");
//                    output[1] = "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + "#" + ste.getMethodName();
//                }
//                showLogInfo(tag, level, output[1], message);
//            } else {
//                showLogInfo(tag, level, output[1], message);
//            }
//        }
//    }
    private static void showLogInfo(String tag, String level, String output, String message) {
        switch (level) {
            case "i":
                Log.i(tag == null ? TAG : tag, output + "  " + message);
                break;
            case "d":
                Log.d(tag == null ? TAG : tag, output + "  " + message);
                break;
            case "v":
                Log.v(tag == null ? TAG : tag, output + "  " + message);
                break;
            case "w":
                Log.w(tag == null ? TAG : tag, output + "  " + message);
                break;
            case "e":
                Log.e(tag == null ? TAG : tag, output + "  " + message);
                break;
            case "json":
                String msg;
                try {
                    if (message.startsWith("{")) {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
                        msg = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
                    } else if (message.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(message);
                        msg = jsonArray.toString(4);
                    } else {
                        msg = message;
                    }
                } catch (JSONException e) {
                    msg = output;
                }
                msg = System.getProperty("line.separator") + msg;
                String[] lines = msg.split(System.getProperty("line.separator"));
                printLine(tag == null ? TAG : tag, output, true);
                for (String line : lines) {
                    Log.e(tag == null ? TAG : tag, output + " ║ " + line);
                }
                printLine(tag == null ? TAG : tag, output, false);
                break;
            default:
                Log.i(tag == null ? TAG : tag, output);
        }
    }

    private static void printLine(String tag, String output, boolean isTop) {
        if (isTop) {
            Log.e(tag == null ? TAG : tag, output + " ╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.e(tag == null ? TAG : tag, output + " ╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    /**
     * 得到一个信息的详细的情况【类名+方法名+行号】
     *
     * @param message 要显示的信息
     * @return 一个信息的详细的情况【类名+方法名+行号+message】
     */
    private static String getDetailMessage(String message) {
        String detailMessage = "";
//        for (StackTraceElement ste : (new Throwable()).getStackTrace()) {
//            //栈顶肯定是LogUtil这个类自己
//            if (LogUtil.class.getName().equals(ste.getClassName())) {
//                continue;
//            }
//            //栈顶的下一个就是需要调用这个类的地方[此处取出类名和方法名还有行号]
//            else {
//                int b = ste.getClassName().lastIndexOf(".") + 1;
//                String TAG = ste.getClassName().substring(b);
//                detailMessage = TAG + "->" + ste.getMethodName() + "():" + ste.getLineNumber()
//                        + "->" + message;
//                break;
//            }
//        }

        StackTraceElement ste = getStackTrace();
        detailMessage = "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + "#" + ste.getMethodName() + "  " + message;
        return detailMessage;
    }

    private static StackTraceElement getStackTrace() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 打印步骤
     */
    public static void showStepLogInfo() {
        android.util.Log.e(TAG + ":", getScope() + "  " + "步骤   " + stepNumber++);
    }

    /**
     * 打印错误信息
     */
    public static void showArgsInfo(Object... args) {
        StringBuilder inputArgs = new StringBuilder();
        inputArgs.append("传入的数据:   ");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i] == null ? "该参数为null" : args[i];
            if (arg instanceof String) {
                if (arg.toString().length() == 0) {
                    arg = "该参数为空字符串\"\"";
                }
                if ("null".equals(arg)) {
                    arg = "该参数为\"null\"字符串";
                }
            }
            inputArgs.append("参数").append(i).append(" = [").append(arg).append("]");
            if (!(i == args.length - 1)) {
                inputArgs.append(",");
            }
        }
        android.util.Log.e(TAG + ":", getScope() + "  " + inputArgs.toString());
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param msg msg
     */
    public static void getChildLog(String msg) {
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String stackTraceMsgArr = stackTraceElement.toString();
            android.util.Log.i(TAG, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
        }
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param tag 日志tag
     * @param msg msg
     */
    public static void getChildLog(String tag, String msg) {
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String stackTraceMsgArr = stackTraceElement.toString();
            android.util.Log.i(tag, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
        }
    }

    /**
     * 打印异常详细信息,推荐使用
     *
     * @param e e
     */
    public static void printExceptionInfo(Exception e) {
        if (isShowLog) {
            android.util.Log.e(TAG + ":", getScope() + "  " + "", e);
        }
    }

    public static void file(String logPath, String content) {
        if (!SDCardUtils.isAvailable() || !SDCardUtils.isHasPermission(MLibs.getContext())) {
            android.util.Log.e(TAG, "读写日志失败,可能没有权限或SD卡有问题");
            return;
        }
        if (!FileUtils.makeDirs(logPath)) {
            android.util.Log.e(TAG, "创建日志文件夹失败!!!");
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

    private static String getScope() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        return "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")" + "#" + trace.getMethodName() + "   ";
    }

    /**
     * debug调试日志
     *
     * @param message 日志信息
     */
    public static void debug(String tag, String message) {

        if (isShowLog && MLibs.isDebug()) {
            Log.i(tag, message);
        }
    }

    /**
     * 锁定日志(出现异常紧急锁定日志)
     *
     * @param callBack 振动回调
     */
    public static void lockLog(@NonNull ICallBack callBack) {
        SensorManager sensorManager = (SensorManager) MLibs.getContext().getSystemService(SENSOR_SERVICE);   //传感器管理器
        Sensor sensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER);     //此处传入1 也可以，Sensor中加速度传感器对应的int值为1

        //参数:第一个是监听器，第二个是加速度传感器，第三个是传感器的灵敏度 从上往下灵敏度依次降低(SENSOR_DELAY_FASTEST , SENSOR_DELAY_GAME, SENSOR_DELAY_UI, SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        iCallBack = callBack;
//        vibrator = (Vibrator) MLibs.getContext().getSystemService(VIBRATOR_SERVICE);          //振动器
    }

    private static long lastTime;
    private static SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();  //获取传感器类型
            if (type == 1) {   //等价于  type==TYPE_ACCELEROMETER
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if (Math.abs(x) > 15 && Math.abs(y) > 15 && Math.abs(z) > 15) {     //摇动灵敏度取决于后面的常量值，这里定义了15
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastTime < 1000) {
                        return;
                    }
                    lastTime = currentTimeMillis;
                    if (iCallBack != null) {
                        iCallBack.back();
                        VibratorUtil.vibrate(MLibs.getContext(), 500);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //灵敏度变化时调用。灵敏度级别参考：SensorManager.SENSOR_DELAY_GAME
        }
    };
    private static ICallBack iCallBack;


    /**
     * 计时
     */
    private static long timingStart;

    /**
     * 开始计时
     */
    public static void startTiming(String exStr) {
        timingStart = System.currentTimeMillis();
        Log.i("调试信息", String.format("%s 开始计时:  %s", exStr, DateUtil.formatDate(DateUtil.REGEX_DATE_TIME_MILL, timingStart)));
    }

    /**
     * 结束计时
     */
    public static void endTiming(String exStr) {
        long timingEnd = System.currentTimeMillis();
        Log.i("调试信息", String.format("%s 停止计时:  %s 耗时: %d秒", exStr, DateUtil.formatDate(DateUtil.REGEX_DATE_TIME_MILL, timingEnd), timingEnd - timingStart));
    }


    public static void jsonObj(String tag, Object obj) {
        printLongLog(tag, GsonUtil.toJson(obj));
    }

    /**
     * https://juejin.cn/post/6887066905967951879
     */
    public static void printLongLog(String tag, String msg) {
        if (!isShowLog) {
            return;
        }
        msg = msg.replace("(", "（").replace(")", "）");
        // 获取调用位置
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callPosition = "";
        if (stackTrace.length >= 4) {
            StackTraceElement element = stackTrace[4];
            callPosition = "(" + element.getFileName() + ":" + element.getLineNumber() + ") ";
        }
        msg = callPosition + msg;
        // 1. 测试控制台最多打印4062个字节，不同情况稍有出入（注意：这里是字节，不是字符！！）
        // 2. 字符串默认字符集编码是utf-8，它是变长编码一个字符用1~4个字节表示
        // 3. 这里字符长度小于1000，即字节长度小于4000，则直接打印，避免执行后续流程，提高性能哈
        if (msg.length() < 1000) {
            Log.println(Log.INFO, tag, msg);
            return;
        }

        // 一次打印的最大字节数
        int maxByteNum = 4000;

        // 字符串转字节数组
        byte[] bytes = msg.getBytes();

        // 超出范围直接打印
        if (maxByteNum >= bytes.length) {
            Log.println(Log.INFO, tag, msg);
            return;
        }

        // 分段打印计数
        int count = 1;

        // 在数组范围内，则循环分段
        while (maxByteNum < bytes.length) {
            // 按字节长度截取字符串
            String subStr = cutStr(bytes, maxByteNum);

            // 打印日志
//            String desc = String.format("分段打印(%s):%s", count++, subStr);
//            Log.println(priority, tag, desc);
//            String desc = String.format(subStr);
            Log.println(Log.INFO, tag, subStr);

            // 截取出尚未打印字节数组
            bytes = Arrays.copyOfRange(bytes, subStr.getBytes().length, bytes.length);

            // 可根据需求添加一个次数限制，避免有超长日志一直打印
            /*if (count == 10) {
                break;
            }*/
        }

        // 打印剩余部分
//        Log.println(Log.INFO, tag, String.format("分段打印(%s):%s", count, new String(bytes)));
        Log.println(Log.INFO, tag, new String(bytes));
    }

    public static String formateDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault()).format(new Date());
    }

    public static void printArgs(String tag, String... args) {
        Log.i(tag, Arrays.asList(args).toString());
    }

    public static void writeToFile(String data, String filePath) {
        try {
            // 创建文件对象
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            // 如果文件不存在，则创建新文件
            if (!file.exists()) {
                file.createNewFile();
            }

            // 创建输出流对象
            FileOutputStream fos = new FileOutputStream(file, true);

            // 将字符串转换为字节数组
            byte[] bytes = data.getBytes();

            // 写入数据
            fos.write(bytes);

            // 关闭输出流
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param access_token 67efb4e356cb5c10aa41c7fcfc58102df8432b187cb7441f2e1a7f04ce62a503
     * @param tag          log
     * @param content      内容
     * @param iddClick     回调
     */
    private static void pushDdMsg(String access_token, String tag, String content, IDDClick iddClick) {
        String url = "https://oapi.dingtalk.com/robot/send?access_token=" + access_token;

        String json = "{\"text\":{\"content\":\"" + tag + ": " + content + "\"},\"msgtype\":\"text\"}";
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (iddClick != null) {
                    iddClick.onClick(e.getMessage());
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (iddClick != null) {
                    if (response.isSuccessful()) {
                        iddClick.onClick("成功: " + response.body().string());
                    } else {
                        iddClick.onClick("失败: " + response.message());
                    }
                }
            }
        });
    }


    public interface IDDClick {
        void onClick(String str);
    }

    private static void postJsonAsync(String url, String json) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("调试信息", "onResponse: 成功: " + response.body().string());
                } else {
                    Log.i("调试信息", "onResponse: 失败: " + response.message());
                }
            }
        });
    }

    /**
     * 按字节长度截取字节数组为字符串
     *
     * @param bytes
     * @param subLength
     * @return
     */
    private static String cutStr(byte[] bytes, int subLength) {
        // 边界判断
        if (bytes == null || subLength < 1) {
            return null;
        }

        // 超出范围直接返回
        if (subLength >= bytes.length) {
            return new String(bytes);
        }

        // 复制出定长字节数组，转为字符串
        byte[] subBytes = Arrays.copyOf(bytes, subLength);

        // 检查最后一个字节是否为多字节字符的一部分
        int i = subBytes.length - 1;
        while (i > 0 && (subBytes[i] & 0xC0) == 0x80) {
            i--;
        }
        // 如果最后一个字节是多字节字符的一部分，向前移动到完整的字符
        if (i < subBytes.length - 1) {
            subBytes = Arrays.copyOf(subBytes, i);
        }
        return new String(subBytes);
    }

    //设定Json格式化时候的缩进量
    private static final int JSON_INDENT = 4;

    public static void json(String json) {
        if (json == null) {
            Log.d(TAG, "Null or Empty JSON was returned.");
            return;
        }
        String message;
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message = jsonObject.toString(JSON_INDENT);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        }
        printLog(message);
    }

    private static void printLog(String message) {
        int maxLogSize = 1000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d(TAG, message.substring(start, end));
//            printLongLog(TAG, message);
        }
    }

    private static void isNull(Objects objects) {
        Log.i("调试信息", "isNull:  " + objects);
    }

}