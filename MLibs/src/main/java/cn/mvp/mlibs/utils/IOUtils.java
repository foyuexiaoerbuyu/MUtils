package cn.mvp.mlibs.utils;

import java.io.Closeable;
import java.io.IOException;

import cn.mvp.mlibs.log.LogUtils;

/**
 * @author wlj
 * @date 2017/3/29
 * @email wanglijundev@gmail.com
 * @packagename wanglijun.vip.androidutils.utils
 * @desc: io操作
 */

public class IOUtils {
    /**
     * Close closeable object
     * 关闭可以关闭的对象
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LogUtils.e("调试信息", "close: ", e);
//                LogUtils.d("IOUtils",e.toString());
            }
        }
    }

}