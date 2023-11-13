package cn.mvp.mlibs.utils;

import java.io.Closeable;
import java.io.IOException;

import cn.mvp.mlibs.log.XLogUtil;

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
                XLogUtil.e("调试信息", "close: ", e);
//                XLogUtil.d("IOUtils",e.toString());
            }
        }
    }

}