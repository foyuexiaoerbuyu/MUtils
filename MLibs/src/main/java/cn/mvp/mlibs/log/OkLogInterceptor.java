package cn.mvp.mlibs.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

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
public class OkLogInterceptor implements Interceptor {
    public static final String TAG = "OkHttpUtils";
    private boolean isShowLog;
    private String tag;
    private boolean showResponse;

    public OkLogInterceptor(String tag, boolean isShowLog, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = "OkHttpUtils";
        }

        this.showResponse = showResponse;
        this.isShowLog = isShowLog;
        this.tag = tag;
    }

    public OkLogInterceptor(String tag, boolean isShowLog) {
        if (TextUtils.isEmpty(tag)) {
            tag = "OkHttpUtils";
        }

        this.isShowLog = isShowLog;
        this.tag = tag;
    }

    public OkLogInterceptor(String tag) {
        this(tag, true);
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
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
        printLog(tag, message, Log.DEBUG);
    }

    public static void e(String tag, String message) {
        printLog(tag, message, Log.ERROR);
    }

    public static void i(String tag, String message) {
        printLog(tag, message, Log.INFO);
    }

    public static void v(String tag, String message) {
        printLog(tag, message, Log.VERBOSE);
    }

    public static void w(String tag, String message) {
        printLog(tag, message, Log.WARN);
    }

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
        if (isShowLog) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
            String stackTraceMsgArr = stackTraceElement.toString();
            android.util.Log.i(tag, stackTraceMsgArr.substring(stackTraceMsgArr.indexOf("(")) + "#" + stackTraceElement.getMethodName() + " msg:" + msg);
        }
    }
}
