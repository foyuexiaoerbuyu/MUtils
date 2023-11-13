package cn.mvp.mlibs.download;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.TimerTask;


/**
 * https://github.com/hamuamu0/DownloadManagerDemo
 * https://toutiao.io/posts/488752/app_preview
 * https://blog.51cto.com/u_5018054/3402655
 * https://www.123si.org/android/article/a-brief-introduction-to-download-manager/
 * 类或接口的描述信息
 * <p>
 * 注意9.0以后不允许明文通信  需要增加     android:usesCleartextTraffic="true"
 *
 * @Author:qubin
 * @Theme: 更新
 * @Data:2018/12/17
 * @Describe: 使用Android默认的DownloadManager下载文件
 */
public class DownLoadBuilder {

    private static TimerTask task;
    private static long mTaskId;
    private static DownloadManager downloadManager;
    //广播接受者，接收下载状态
    private static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };
    private Context context;
    private String url;
    private Boolean isWiFi;
    private String description;
    private String downloadName;


    public DownLoadBuilder(Builder builder) {
        super();
        context = builder.context;
        url = builder.url;
        isWiFi = builder.isWiFi;
        description = builder.description;
        downloadName = builder.downloadName;
        if (isWiFi == null) {
            isWiFi = true;
        }
        download(context, url, isWiFi, description, downloadName);
    }

    public static void download(final Context context, final String url, final boolean isWIFI, final String description, final String downloadName) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(context, "下载地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadApk(context, url, isWIFI, description, downloadName);
        } else {
            boolean b = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
            if (b) {
                downloadApk(context, url, isWIFI, description, downloadName);
            } else {
                Toast.makeText(context, "权限未开启", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //检查下载状态
    private static void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (columnIndex < 0) {
                Log.i("调试信息", "查询错误:  " + columnIndex);
                c.close();
                return;
            }
            int status = c.getInt(columnIndex);
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.i("调试信息", ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.i("调试信息", ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.i("调试信息", ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i("调试信息", ">>>下载完成");
                    //下载完成安装APK
                    //downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
//                    installAPK(new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                    // 查找暂停原因
                    int reason = c.getInt(reasonIdx);
                    String reasonString;
                    switch (reason) {
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            reasonString = "Waiting for WiFi";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            reasonString = "Waiting for connectivity";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            reasonString = "Waiting to retry";
                            break;
                        default:
                            reasonString = "其他错误: " + reason;
                            break;
                    }
                    Log.i("调试信息", ">>>下载失败 " + reasonString);
                    break;
                default:
                    Log.i("调试信息", "checkDownloadStatus未知:  " + status);
            }
        }
        c.close();
    }

    private static void downloadApk(Context context, String url, boolean isWIFI, String description, String downloadName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            //判断是在wifi还是在移动网络下进行下载
            if (isWIFI) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            } else {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            }
            //下载时显示notification
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //添加描述信息
            request.setDescription(description);
            //file:///storage/emulated/0/Download/downloadName.apk
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadName + ".apk");

            request.setMimeType("application/vnd.android.package-archive");
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mTaskId = downloadManager.enqueue(request);
            //注册广播接收者，监听下载状态
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("调试信息", "downloadApk:  ", e);
            //防止崩溃，再抓一下
            try {//携带下载链接跳转到浏览器
                if (!TextUtils.isEmpty(url) && url.contains("http")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    context.startActivity(intent);
                }

            } catch (Exception e1) {
                //异常处理
                e1.printStackTrace();
                Log.e("调试信息", "downloadApk--:  ", e1);
            }
        }
    }

    public static void intallApk(Context context, String downloadName) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //6.0及以下安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file:///storage/emulated/0/Download/" + downloadName + ".apk"), "application/vnd.android.package-archive");
            //为这个新apk开启一个新的activity栈
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //开始安装
            context.startActivity(intent);

        } else {
            //7.0及以上
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    , "/" + downloadName + ".apk");
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(context, "com.qubin.downloadmanager", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(intent);


        }
    }

    public static final class Builder {

        private Context context;
        private String url;
        private Boolean isWiFi;
        private String description;
        private String downloadName;


        public Builder(Context context) {
            this.context = context;

        }

        public Builder addUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder isWiFi(Boolean isWiFi) {
            this.isWiFi = isWiFi;
            return this;
        }

        public Builder addDscription(String description) {
            this.description = description;
            return this;
        }

        public Builder addDownLoadName(String downloadName) {
            this.downloadName = downloadName;
            return this;
        }

        public DownLoadBuilder builder() {
            return new DownLoadBuilder(this);
        }

    }

}
