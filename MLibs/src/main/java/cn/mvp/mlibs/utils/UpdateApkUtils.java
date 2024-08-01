package cn.mvp.mlibs.utils;

//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.util.Log;
//
//import androidx.appcompat.app.AlertDialog;
//
//import com.blankj.utilcode.util.AppUtils;
//import com.blankj.utilcode.util.GsonUtils;
//import com.blankj.utilcode.util.ToastUtils;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.FileCallBack;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import java.io.File;
//
//import okhttp3.Call;
//import okhttp3.Request;

/**
 * 检查更新app 检测更新app
 * <p>
 * {"code":200,"msg":null,"data":{"versionCode":15,"versionName":"1.0.15","updateInfo":"Newfeaturesandbugfixes.","apkUrl":"http://XX.xx.XXX:2324/test/download/-1215205374","forceUpdate":false}}
 * <p>
 * compile 'com.zhy:okhttputils:2.6.2'
 * //    implementation 'com.blankj:utilcode:1.30.7'//Android兼容包
 * implementation 'com.blankj:utilcodex:1.31.1'
 * <p>
 * 8.0及以上需要增加
 * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
 * <p>
 * // 处理权限请求的回调
 *
 * @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
 * if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
 * // 如果请求被用户允许，grantResults 列表不为空且第一个元素为 PERMISSION_GRANTED
 * if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
 * // 权限被授予，执行相应操作
 * } else {
 * // 权限被拒绝，可以给用户解释为什么需要这个权限，或者选择其他操作
 * }
 * }
 * }
 */
public class UpdateApkUtils {
//    private static final String TAG = "UpdateUtils";
//    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1122;
//
//    public static void checkUpdate(Activity context, String checkUpdateUrl) {
//        ToastUtils.showShort("这个是服务端测试包");
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            List<String> deniedPermissions = new ArrayList<>();
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                if (!context.getPackageManager().canRequestPackageInstalls()) {
////                    deniedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
////                }
////            }
////            if (ContextCompat.checkSelfPermission(context,
////                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////                deniedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
////            }
////            if (ContextCompat.checkSelfPermission(context,
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////                deniedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
////            }
////            if (deniedPermissions.size() > 0) {
////                context.requestPermissions(deniedPermissions.toArray(new String[]{}), 100);
////                return;
////            }
////        }
//
//        //如果想要静默安装必须是系统应用且有这个安装权限
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
////                ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
////            // 请求权限
////            ToastUtils.showShort("无安装apk权限");
////            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_CODE_STORAGE_PERMISSION);
////            return;
////        }
//        OkHttpUtils.get().url(checkUpdateUrl)
//                .build().execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        ToastUtils.showShort("更新失败:" + e.getLocalizedMessage());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int i) {
//                        Log.i(TAG, "onResponse: " + response + "  " + AppUtils.getAppVersionCode());
//                        ResponseData responseData = GsonUtils.fromJson(response, ResponseData.class);
//                        if (responseData.getCode() == 200) {
//                            if (AppUtils.getAppVersionCode() >= responseData.getData().getVersionCode()) {
//                                ToastUtils.showShort("暂时无更新");
//                                return;
//                            }
//                            new AlertDialog.Builder(context)
//                                    .setMessage("检测到新版本是否更新")
//                                    .setNegativeButton("确定", (dialog, which) -> {
//                                        startUpdate(responseData.getData().getApkUrl());
//                                    }).show();
//                        } else {
//                            ToastUtils.showShort(responseData.getMsg());
//                        }
//                    }
//
//                    private void startUpdate(String apkUrl) {
////                        File filesDir = SDCardUtils.getInternalFilesDir(context);
//                        File filesDir = context.getFilesDir();
//                        ProgressDialog dialog = new ProgressDialog(context);
//                        dialog.setTitle("提示");
//                        dialog.setMessage("正在更新...");
//                        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//
//                        dialog.setMax(100);
//                        dialog.setProgress(0);
//                        dialog.dismiss();
//                        dialog.setOnCancelListener(dialog1 -> {
//                            File file = new File(filesDir.getPath(), "newApk.apk");
//                            OkHttpUtils.getInstance().cancelTag("dowApkReq");
//                            if (file.exists() && file.delete()) {
//                                Log.e(TAG, "下载弹框取消时,回调删除下载文件", null);
//                            }
//                        });
//                        dialog.show();
//                        OkHttpUtils.get().url(apkUrl).tag("dowApkReq").build()
//                                .execute(new FileCallBack(filesDir.getPath(), "newApk.apk") {
//
//                                    @Override
//                                    public void onBefore(Request request, int id) {
//                                        super.onBefore(request, id);
//                                        File file = new File(filesDir.getPath(), "newApk.apk");
//                                        if (file.exists() && file.delete()) {
//                                            Log.e(TAG, "删除遗留文件", null);
////                                            ToastUtils.showShort("删除遗留文件");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void inProgress(float progress, long total, int id) {
//                                        super.inProgress(progress, total, id);
//                                        int currProgress = (int) (progress / total * 100);
//                                        Log.i(TAG, "inProgress: " + currProgress + "  " + progress + "  " + total + "  " + (int) (progress * 100));
//                                        dialog.setProgress((int) (progress * 100));
//                                    }
//
//                                    @Override
//                                    public void onError(Call call, Exception e, int i) {
//                                        dialog.dismiss();
//                                    }
//
//                                    @Override
//                                    public void onResponse(File file, int i) {
//                                        dialog.dismiss();
//                                        new AlertDialog.Builder(context)
//                                                .setMessage("是否安装")
//                                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        AppUtils.installApp(file);
//                                                    }
//                                                })
//                                                .show();
//                                    }
//                                });
//                    }
//                });
//    }
//}
//
//
//class ResponseData {
//    private int code;
//    private String msg;
//    private AppUpdateInfo data;
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public AppUpdateInfo getData() {
//        return data;
//    }
//
//    public void setData(AppUpdateInfo data) {
//        this.data = data;
//    }
//}
//
//class AppUpdateInfo {
//    private int versionCode;
//    private String versionName;
//    private String updateInfo;
//    private String apkUrl;
//    private boolean forceUpdate;
//
//    // Constructors
//    public AppUpdateInfo() {
//    }
//
//    public AppUpdateInfo(int versionCode, String versionName, String updateInfo, String apkUrl, boolean forceUpdate) {
//        this.versionCode = versionCode;
//        this.versionName = versionName;
//        this.updateInfo = updateInfo;
//        this.apkUrl = apkUrl;
//        this.forceUpdate = forceUpdate;
//    }
//
//    // Getters and Setters
//    public int getVersionCode() {
//        return versionCode;
//    }
//
//    public void setVersionCode(int versionCode) {
//        this.versionCode = versionCode;
//    }
//
//    public String getVersionName() {
//        return versionName;
//    }
//
//    public void setVersionName(String versionName) {
//        this.versionName = versionName;
//    }
//
//    public String getUpdateInfo() {
//        return updateInfo;
//    }
//
//    public void setUpdateInfo(String updateInfo) {
//        this.updateInfo = updateInfo;
//    }
//
//    public String getApkUrl() {
//        return apkUrl;
//    }
//
//    public void setApkUrl(String apkUrl) {
//        this.apkUrl = apkUrl;
//    }
//
//    public boolean isForceUpdate() {
//        return forceUpdate;
//    }
//
//    public void setForceUpdate(boolean forceUpdate) {
//        this.forceUpdate = forceUpdate;
//    }
//
//    // toString method (optional, for debugging)
//    @Override
//    public String toString() {
//        return "AppUpdateInfo{" +
//                "versionCode=" + versionCode +
//                ", versionName='" + versionName + '\'' +
//                ", updateInfo='" + updateInfo + '\'' +
//                ", apkUrl='" + apkUrl + '\'' +
//                ", forceUpdate=" + forceUpdate +
//                '}';
//    }
}
//public interface DownloadListener {
//        void onDownloadSuccess(File file);
//
//        void onDownloadFailed(Exception e);
//
//        default void onDownloading(int progress) {
//
//        }
//    }
//
//
//    public static void downloadFile(String url, final String saveDir, final DownloadListener listener) {
//        Request request = new Request.Builder().url(url).build();
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "onFailure下载文件失败:  ", e);
//                if (listener != null) {
//                    listener.onDownloadFailed(e);
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = null;
//                FileOutputStream fos = null;
//                try {
//                    inputStream = response.body().byteStream();
//                    long total = response.body().contentLength();
//                    File file = new File(saveDir, getFileName(url));
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                    fos = new FileOutputStream(file);
//
//                    byte[] buffer = new byte[2048];
//                    int len;
//                    long sum = 0;
//                    while ((len = inputStream.read(buffer)) != -1) {
//                        fos.write(buffer, 0, len);
//                        sum += len;
//                        int progress = (int) (sum * 1.0f / total * 100);
//                        // 下载中
//                        listener.onDownloading(progress);
//                    }
//                    fos.flush();
//
//                    if (listener != null) {
//                        listener.onDownloadSuccess(file);
//                    }
//                } catch (IOException e) {
//                    if (listener != null) {
//                        listener.onDownloadFailed(e);
//                    }
//                } finally {
//                    try {
//                        if (inputStream != null) {
//                            inputStream.close();
//                        }
//                        if (fos != null) {
//                            fos.close();
//                        }
//                    } catch (IOException e) {
//                        if (listener != null) {
//                            listener.onDownloadFailed(e);
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    private static String getFileName(String url) {
//        return url.substring(url.lastIndexOf("/") + 1);
//    }
