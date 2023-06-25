package cn.mvp.mlibs.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * // https://mvnrepository.com/artifact/com.squareup.okhttp3/logging-interceptor
 * implementation 'com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11'
 * 需要添加okhttp3,okio相关包
 * // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
 * implementation 'com.squareup.okhttp3:okhttp:3.14.9'
 * OKhttp日志拦截
 */
public class OkLogs implements Interceptor {
    public static final String TAG = "OkHttpUtils";
    private boolean isShowLog;
    private String tag;
    private boolean showResponse;

    public OkLogs(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = "OkHttpUtils";
        }

        this.showResponse = showResponse;
        this.tag = tag;
    }

    public OkLogs(String tag) {
        this(tag, true);
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        this.logForRequest(request);
        Response response = chain.proceed(request);
        return this.logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            e(this.tag, "========response'log=======");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            e(this.tag, "url : " + clone.request().url());
            e(this.tag, "code : " + clone.code());
            e(this.tag, "protocol : " + clone.protocol());
            if (!TextUtils.isEmpty(clone.message())) {
                e(this.tag, "message : " + clone.message());
            }

            if (this.showResponse) {
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        e(this.tag, "responseBody's contentType : " + mediaType.toString());
                        if (this.isText(mediaType)) {
                            String resp = body.string();
                            e(this.tag, "responseBody's content : " + resp);
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        }

                        e(this.tag, "responseBody's content :  maybe [file part] , too large too print , ignored!");
                    }
                }
            }

            e(this.tag, "========response'log=======end");
        } catch (Exception var7) {
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();
            e(this.tag, "========request'log=======");
            e(this.tag, "method : " + request.method());
            e(this.tag, "url : " + url);
            if (headers != null && headers.size() > 0) {
                e(this.tag, "headers : " + headers.toString());
            }

            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    e(this.tag, "requestBody's contentType : " + mediaType.toString());
                    if (this.isText(mediaType)) {
                        e(this.tag, "requestBody's content : " + this.bodyToString(request));
                    } else {
                        e(this.tag, "requestBody's content :  maybe [file part] , too large too print , ignored!");
                    }
                }
            }

            e(this.tag, "========request'log=======end");
        } catch (Exception var6) {
        }

    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        } else {
            return mediaType.subtype() != null && (mediaType.subtype().equals("json") || mediaType.subtype().equals("xml") || mediaType.subtype().equals("html") || mediaType.subtype().equals("webviewhtml"));
        }
    }

    private String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException var4) {
            return "something error when show requestBody.";
        }
    }

    private static final int MAX_LOG_LENGTH = 4000;

    public static void d(String tag, String message) {
        print(tag, message, Log.DEBUG);
    }

    public static void e(String tag, String message) {
        print(tag, message, Log.ERROR);
    }

    public static void i(String tag, String message) {
        print(tag, message, Log.INFO);
    }

    public static void v(String tag, String message) {
        print(tag, message, Log.VERBOSE);
    }

    public static void w(String tag, String message) {
        print(tag, message, Log.WARN);
    }

    /**
     * 也能用 但是过长会打印不全 https://www.jianshu.com/p/a491d843fa19
     */
    private static void printLog(String tag, String message, int logType) {
        // 获取调用位置
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callPosition = "";
        if (stackTrace.length >= 4) {
            StackTraceElement element = stackTrace[4];
            callPosition = "(" + element.getFileName() + ":" + element.getLineNumber() + ") ";
        }

        // 分段打印日志
        int logLength = message.length();
        int start = 0;
        int end = MAX_LOG_LENGTH;
        while (start < logLength) {
            if (end > logLength) {
                end = logLength;
            }
            String logContent = callPosition + message.substring(start, end);
            switch (logType) {
                case Log.DEBUG:
                    Log.d(tag, logContent);
                    break;
                case Log.ERROR:
                    Log.e(tag, logContent);
                    break;
                case Log.INFO:
                    Log.i(tag, logContent);
                    break;
                case Log.VERBOSE:
                    Log.v(tag, logContent);
                    break;
                case Log.WARN:
                    Log.w(tag, logContent);
                    break;
                default:
                    break;
            }
            start = end;
            end += MAX_LOG_LENGTH;
        }
    }

    /**
     * 调用处子类方法所在位置
     *
     * @param tag 日志tag
     * @param msg msg
     */
    public void getChildLog(String tag, String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String stackTraceMsgArr = stackTraceElement.toString();
        Log.i(tag, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
    }

    /**
     * https://www.jianshu.com/p/a491d843fa19
     * 打印日志到控制台（解决Android控制台丢失长日志记录）
     *
     * @param priority
     * @param tag
     * @param content
     */
    public static void print(String tag, String content, int priority) {
        // 获取调用位置
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callPosition = "";
        if (stackTrace.length >= 4) {
            StackTraceElement element = stackTrace[4];
            callPosition = "(" + element.getFileName() + ":" + element.getLineNumber() + ") ";
        }
        content = callPosition + content;
        // 1. 测试控制台最多打印4062个字节，不同情况稍有出入（注意：这里是字节，不是字符！！）
        // 2. 字符串默认字符集编码是utf-8，它是变长编码一个字符用1~4个字节表示
        // 3. 这里字符长度小于1000，即字节长度小于4000，则直接打印，避免执行后续流程，提高性能哈
        if (content.length() < 1000) {
            Log.println(priority, tag, content);
            return;
        }

        // 一次打印的最大字节数
        int maxByteNum = 4000;

        // 字符串转字节数组
        byte[] bytes = content.getBytes();

        // 超出范围直接打印
        if (maxByteNum >= bytes.length) {
            Log.println(priority, tag, content);
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
            String desc = String.format(subStr);
            Log.println(priority, tag, desc);

            // 截取出尚未打印字节数组
            bytes = Arrays.copyOfRange(bytes, subStr.getBytes().length, bytes.length);

            // 可根据需求添加一个次数限制，避免有超长日志一直打印
            /*if (count == 10) {
                break;
            }*/
        }

        // 打印剩余部分
//        Log.println(priority, tag, String.format("分段打印(%s):%s", count, new String(bytes)));
        Log.println(priority, tag, new String(bytes));
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
        String subStr = new String(Arrays.copyOf(bytes, subLength));

        // 避免末尾字符是被拆分的，这里减1使字符串保持完整
        return subStr.substring(0, subStr.length() - 1);
    }
}
