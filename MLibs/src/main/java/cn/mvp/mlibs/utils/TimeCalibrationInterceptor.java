package cn.mvp.mlibs.utils;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 * author : Haitao
 * blog   : http://blog.nhtzj.com
 * time   : 2018/4/2
 * desc   : 利用OkHttp的Interceptor自动同步时间
 * version: 2.0
 * </pre>
 */
public class TimeCalibrationInterceptor implements Interceptor {
    long minResponseTime = Long.MAX_VALUE;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.nanoTime();
        Response response = chain.proceed(request);
        long responseTime = System.nanoTime() - startTime;

        Headers headers = response.headers();
        calibration(responseTime, headers);
        return response;
    }

    private void calibration(long responseTime, Headers headers) {
        if (headers == null) {
            return;
        }

        //如果这一次的请求响应时间小于上一次，则更新本地维护的时间
        if (responseTime >= minResponseTime) {
            return;
        }

        String standardTime = headers.get("Date");
        if (!TextUtils.isEmpty(standardTime)) {
            Date parse = HttpDate.parse(standardTime);
            if (parse != null) {
                // 客户端请求过程一般大于比收到响应时间耗时，所以没有简单的除2 加上去，而是直接用该时间
                TimeManager.getInstance().initServerTime(parse.getTime());
                minResponseTime = responseTime;
            }
        }
    }

}