package cn.mvp.mlibs.download;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.mvp.mlibs.utils.FileUtils;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lhc on 2017/6/21.
 * 1.应该先判断下载的文件是否存在
 * 2.监听的接口应该抛到主线程中。
 * <p>
 * 1.怎么判断SD卡是否存在，是否已经满了？ Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
 * 2.怎么创建SD卡二级目录？              new File(Environment.getExternalStorageDirectory(), saveDir)
 * 3.怎么得到文件名？                    url.substring(url.lastIndexOf("/") + 1);
 * 4.怎么根据文件名创建File
 * 5.怎么输出到文件
 * <p>
 * 注意9.0以后不允许明文通信  需要增加     android:usesCleartextTraffic="true"
 */

public class DownloadUtil {

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    /**
     * @param url      下载连接
     * @param filePath 文件路径
     * @param listener 下载监听
     */
    public void download(final String url, final String filePath, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    FileUtils.makeDirs(filePath);
//                    String savePath = isExistDir(saveDir == null ? SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" : saveDir);
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(filePath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed(e);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                // 下载失败
                listener.onDownloadFailed(e);

            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File downloadFile = new File(saveDir);
            Log.i("调试信息", "isExistDir:  " + downloadFile.getPath() + "  " + saveDir);
            if (!downloadFile.exists() && !downloadFile.mkdirs()) {
                downloadFile.createNewFile();
            }
            String savePath = downloadFile.getAbsolutePath();
            Log.e("savePath", savePath);
            return savePath;
        }
        return null;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(Exception e);
    }
}
