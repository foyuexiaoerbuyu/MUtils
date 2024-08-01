package cn.mvp.mlibs.utils;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <!-- https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp -->
 * <dependency>
 * <groupId>com.squareup.okhttp3</groupId>
 * <artifactId>okhttp</artifactId>
 * <version>3.14.9</version>
 * </dependency>
 * 网络请求
 *
 * @lastUpdate 20231122_180420
 */
public class OkHttpUtil {

    // 创建OkHttpClient对象
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
            .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间
            .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间
            .build();

    /**
     * GET请求
     *
     * @param url 请求URL
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * POST请求
     *
     * @param url         请求URL
     * @param requestBody 请求体
     * @param headers     请求头
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String post(String url, RequestBody requestBody, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 构造JSON请求体
     *
     * @param jsonStr JSON字符串
     * @return JSON请求体
     */
    public static RequestBody buildJsonRequestBody(String jsonStr) {
        return RequestBody.create(MediaType.parse("application/json"), jsonStr);
    }

    public static RequestBody buildJsonRequestBody(Object jsonStr) {
        return RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(jsonStr));
    }

    /**
     * 构造表单请求体
     *
     * @param formParams 表单参数
     * @return 表单请求体
     */
    public static RequestBody buildFormRequestBody(Map<String, String> formParams) {
        FormBody.Builder builder = new FormBody.Builder();
        if (formParams != null && formParams.size() > 0) {
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 构造Multipart请求体
     *
     * @param multipartParams Multipart参数
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBody(Map<String, Object> multipartParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (multipartParams != null && multipartParams.size() > 0) {
            for (Map.Entry<String, Object> entry : multipartParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof byte[]) {
                    builder.addFormDataPart(key, null,
                            RequestBody.create(MediaType.parse("application/octet-stream"), (byte[]) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(key, null, (RequestBody) value);
                }
            }
        }
        return builder.build();
    }

    /**
     * 构造Multipart请求体，上传文件
     *
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBodyWithFile(File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
    }

    /**
     * 构造Multipart请求体，支持上传文件
     *
     * @param multipartParams Multipart参数
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBodyWithFiles(Map<String, Object> multipartParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (multipartParams != null && multipartParams.size() > 0) {
            for (Map.Entry<String, Object> entry : multipartParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof byte[]) {
                    builder.addFormDataPart(key, null,
                            RequestBody.create(MediaType.parse("application/octet-stream"), (byte[]) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(key, null, (RequestBody) value);
                } else if (value instanceof UploadFile) { // 支持上传文件
                    UploadFile file = (UploadFile) value;
                    builder.addFormDataPart(key, file.getName(),
                            RequestBody.create(MediaType.parse(file.getMimeType()), file.getFile()));
                }
            }
        }
        return builder.build();
    }

    /**
     * 上传文件
     *
     * @param url     请求URL
     * @param file    上传的文件
     * @param headers 请求头
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String uploadFile(String url, UploadFile file, Map<String, String> headers) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(file.getMimeType()), file.getFile()))
                .build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * https://www.jianshu.com/p/3b269082cbbb
     *
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public static void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, url.substring(url.lastIndexOf("/") + 1));
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
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         *
         * @param file
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         *
         * @param e
         */
        void onDownloadFailed(Exception e);
    }

    /**
     * 封装文件上传参数
     */
    public static class UploadFile {
        private String name;
        private String mimeType;
        private byte[] file;

        public UploadFile(String filePath) {
            File file = new File(filePath);
            // 读取文件内容
            byte[] fileBytes = new byte[0];
            try {
                fileBytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.name = file.getName();
            this.mimeType = "text/plain";
            this.file = fileBytes;
        }

        public UploadFile(String name, String mimeType, byte[] file) {
            this.name = name;
            this.mimeType = mimeType;
            this.file = file;
        }

        public String getName() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }

        public byte[] getFile() {
            return file;
        }
    }
}