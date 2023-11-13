package cn.mvp.mlibs.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.DecimalFormat;

import cn.mvp.mlibs.log.XLogUtil;
import cn.mvp.mlibs.utils.ents.FileInfos;
import kotlin.Deprecated;

/**
 * @author： wlj
 * @Date: 2017-03-28
 * @email: wanglijundev@gmail.com
 * @desc: 文件工具类
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    public final static String FILE_SUFFIX_SEPARATOR = ".";

    /**
     * Read file
     *
     * @param filePath    路径
     * @param charsetName
     * @return
     */
    public static String readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder();
        if (!isFileExist(filePath)) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("调试信息", "读取文件异常", e);
            return null;
        } finally {
            IOUtils.close(reader);
        }
    }

    /**
     * Write file
     *
     * @param filePath 路径
     * @param content
     * @param append
     * @return
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            IOUtils.close(fileWriter);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileWriter);
        }
        return false;
    }

    /**
     * write file, the string will be written to the begin of the file
     *
     * @param filePath 路径
     * @param content
     * @return
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * 写文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @return
     */
    public static boolean writeFile(String filePath, InputStream is) {
        return writeFile(filePath, is, false);
    }

    /**
     * 写文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @param append   追加
     * @return
     */
    public static boolean writeFile(String filePath, InputStream is, boolean append) {
        if (filePath == null) {
            return false;
        }
        return writeFile(new File(filePath), is, append);
    }

    /**
     * Write file
     *
     * @param file
     * @param is   输入流
     * @return
     */
    public static boolean writeFile(File file, InputStream is) {
        return writeFile(file, is, false);
    }

    /**
     * Write file
     *
     * @param file   .
     * @param is     输入流     .
     * @param append .
     * @return .
     */
    public static boolean writeFile(File file, InputStream is, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = is.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("调试信息", "写入文件异常:  ", e);
        } finally {
            IOUtils.close(o);
            IOUtils.close(is);
        }
        return false;
    }


    public static void writeFile(File file, byte[] recordBuf, boolean append) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(file, append);
            o.write(recordBuf, 0, recordBuf.length);
            o.flush();
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }


    public static void writeFile(String filePath, byte[] byteArray, boolean append) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file, append); // Open the file in append mode
             FileChannel fileChannel = fos.getChannel()) {
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写文件失败,请重试...  " + e.getMessage());
        }
    }


    public static void writeFile(String filePath, ByteBuffer byteBuffer, boolean append) {
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file, append); // Open the file in append mode
             FileChannel fileChannel = fos.getChannel()) {
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            XLogUtil.e(TAG, "写文件失败,请重试...  ", e);
        }
    }


    /**
     * 读取源文件内容
     *
     * @param filename String 文件路径
     * @return byte[] 文件内容
     * @throws IOException .
     */
    public static byte[] readFile(String filename) throws IOException {

        File file = new File(filename);
        if (filename == null || filename.equals("")) {
            throw new IOException("无效的文件路径");
        }
        long len = file.length();
        byte[] bytes = new byte[(int) len];

        FileInputStream in = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        int r = bufferedInputStream.read(bytes);
        if (r != len)
            throw new IOException("读取文件不正确");
        in.close();
        bufferedInputStream.close();
        return bytes;

    }

    /**
     * 读取源文件内容
     *
     * @param filename String 文件路径
     * @param readSize 读取文件字节长度
     * @throws IOException .
     */
    public static void readFile(String filename, int readSize, IReadByte iReadByte) throws IOException {

        File file = new File(filename);
        if (filename.equals("") || !file.exists()) {
            throw new IOException("无效的文件路径");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[readSize];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                // 处理读取的数据，例如写入到另外的文件中或进行其他操作
                // 这里只是简单地打印读取的字节数
                iReadByte.read(buffer);
                System.out.println("读取了 " + bytesRead + " 字节");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Move file
     *
     * @param srcFilePath
     * @param destFilePath
     */
    public static void moveFile(String srcFilePath, String destFilePath) throws FileNotFoundException {
        if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(destFilePath)) {
            throw new RuntimeException("Both srcFilePath and destFilePath cannot be null.");
        }
        moveFile(new File(srcFilePath), new File(destFilePath));
    }

    /**
     * Move file
     *
     * @param srcFile
     * @param destFile
     */
    public static void moveFile(File srcFile, File destFile) throws FileNotFoundException {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }

    /**
     * Copy file
     *
     * @param srcFilePath  源路径(包括文件名+后缀)
     * @param destFilePath 目标路径(包括文件名+后缀)
     * @return 复制是否成功
     * @throws FileNotFoundException 文件找不到异常
     */
    public static boolean copyFile(String srcFilePath, String destFilePath) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(srcFilePath);
        return writeFile(destFilePath, inputStream);
    }

    /**
     * rename file
     *
     * @param file
     * @param newFileName
     * @return
     */
    public static boolean renameFile(File file, String newFileName) {
        File newFile = null;
        if (file.isDirectory()) {
            newFile = new File(file.getParentFile(), newFileName);
        } else {
            String temp = newFileName
                    + file.getName().substring(
                    file.getName().lastIndexOf('.'));
            newFile = new File(file.getParentFile(), temp);
        }
        return file.renameTo(newFile);
    }

    /**
     * 获取文件名(没有后缀)
     * Get file name without suffix
     * Without:没有
     *
     * @param filePath 路径
     * @return 获取没有后缀的文件名
     */
    public static String getFileNameWithoutSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int suffix = filePath.lastIndexOf(FILE_SUFFIX_SEPARATOR);
        int fp = filePath.lastIndexOf(File.separator);
        if (fp == -1) {
            return (suffix == -1 ? filePath : filePath.substring(0, suffix));
        }
        if (suffix == -1) {
            return filePath.substring(fp + 1);
        }
        return (fp < suffix ? filePath.substring(fp + 1, suffix) : filePath.substring(fp + 1));
    }

    /**
     * 获取文件名(包含后缀)
     * Get file name
     *
     * @param filePath 路径
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int fp = filePath.lastIndexOf(File.separator);
        if (fp == -1) {
            fp = filePath.lastIndexOf("/");
        }
        return (fp == -1) ? filePath : filePath.substring(fp + 1);
    }

    /**
     * Get folder name
     *
     * @param filePath 路径
     * @return
     */
    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int fp = filePath.lastIndexOf(File.separator);
        return (fp == -1) ? "" : filePath.substring(0, fp);
    }

    /**
     * 获取文件后缀
     *
     * @param filePath 路径 文件路径
     * @return 文件后缀(不包含 " . ")
     */
    public static String getFileSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        if (!filePath.contains(FILE_SUFFIX_SEPARATOR)) {
            String[] path = filePath.split(File.separator);
            return path[path.length - 1];
        }
        int suffix = filePath.lastIndexOf(FILE_SUFFIX_SEPARATOR);
        int fp = filePath.lastIndexOf(File.separator);
        if (suffix == -1) {
            return "";
        }
        return (fp >= suffix) ? "" : filePath.substring(suffix + 1);
    }

    /**
     * 创建目录
     *
     * @param filePath 路径
     * @return
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * 判断一个文件是否存在
     *
     * @param filePath 路径 路径
     * @return .
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 判断文件夹是否存在
     *
     * @param directoryPath 路径
     * @return .
     */
    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * 删除文件或文件夹
     *
     * @param path 路径
     * @return .
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return file.delete();
            }
            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
        return file.delete();
    }

    /**
     * 删除文件或文件夹
     *
     * @param file .
     * @return 删除是否成功
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return file.delete();
            }
            for (File f : childFile) {
                deleteFile(f);
            }
        }
        return file.delete();
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }
        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 获取文件夹大小
     *
     * @param file
     * @return
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes 字节
     * @return 返回长度信息信息
     */
    public static String bytes2mb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "MB");
    }


    /**
     * @return 获取到的文件md5值
     */
    public static String getFileMD5(String filePath) {
        if (StringUtil.isBlank(filePath) || !isFileExist(filePath)) {
            return "-0x012345";
        }
        return getFileMD5(new File(filePath));
    }

    /**
     * @return 获取到的文件md5值
     */
    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(in);
        }
    }

    /**
     * 扫描路径下的文件
     *
     * @param file 文件路径
     */
    public static void scanningFilesName(File file) {
        if (file == null) {
            return;
        }
        File[] fs = file.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory()) {    //若是目录，则递归打印该目录下的文件
                    XLogUtil.d("文件夹:" + f.getPath());
                    scanningFilesName(f);
                }
                if (f.isFile())        //若是文件，直接打印
                    XLogUtil.d("文件:" + f.getPath());
            }
        }
    }

    /**
     * 图片格式;bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp,avif
     */
    public boolean isImg(File file) {
        if (file == null) {
            return false;
        }
        String ss = "bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp,avif";
        String[] split = ss.split(",");
        for (String s : split) {
            if (file.getName().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return 文件大小
     */
    public static String getSize(String path) {
        return getSize(new File(path).length());
    }

    /**
     * @return 文件大小
     */
    public static String getSize(File file) {
        return getSize(file.length());
    }

    /**
     * @return 文件大小
     */
    public static String getSize(long size) {
        //获取到的size为：1705230
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "0";
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;
    }

    /**
     * 将文件转换为Base64字符串
     *
     * @param file 文件对象
     * @return Base64编码的字符串
     * @throws IOException 读取文件时可能抛出的异常
     */
    public static String fileToBase64(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        return encodeToString(bytes);
    }

    /**
     * 将Base64字符串转换为文件
     *
     * @param base64Str Base64编码的字符串
     * @param filePath  生成文件的路径
     * @throws IOException 写入文件时可能抛出的异常
     */
    public static void base64ToFile(String base64Str, String filePath) throws IOException {
        byte[] bytes = decode(base64Str);
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    public static String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] decode(String base64Str) {
        return Base64.decode(base64Str, Base64.DEFAULT);
    }

    public static String getFilePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        }
        return path;
    }

    /**
     * 获取文件基本信息
     *
     * @return
     */
    public static FileInfos getFileInfo(String filePath) {

        // 创建File对象
        File file = new File(filePath);
        FileInfos fileInfos = new FileInfos();
        // 获取文件基本信息
        fileInfos.setFileName(file.getName());
        fileInfos.setFileAbsolutePath(file.getAbsolutePath());
        fileInfos.setFileSize(file.length());
        fileInfos.setFile(file.isFile());
        fileInfos.setDirectory(file.isDirectory());
        fileInfos.setHidden(file.isHidden());
        fileInfos.setLastModified(file.lastModified());


        // 获取文件属性
        try {
            Path path = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                path = file.toPath();
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                // 打印文件属性
                fileInfos.setFileTime(attributes.creationTime());
                fileInfos.setFileTime2(attributes.lastAccessTime());
                fileInfos.setFileTime1(attributes.lastModifiedTime());
                fileInfos.setDirectory(attributes.isDirectory());
                fileInfos.setRegularFile(attributes.isRegularFile());
                fileInfos.setSymbolicLink(attributes.isSymbolicLink());
                fileInfos.setOther(attributes.isOther());
                fileInfos.setSize(attributes.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInfos;
    }

    /**
     * String rawJson = FileUtils.readRawJsonFile(this, R.raw.your_raw_json_file);
     * 读取raw文件夹下的json文件
     *
     * @param context 上下文
     * @param resId   raw文件夹下的资源ID
     * @return json文件的内容
     */
    public static String readRawJsonFile(Context context, int resId) {
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            inputStream = context.getResources().openRawResource(resId);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 读取assets文件夹下的json文件
     * String assetsJson = FileUtils.readAssetsJsonFile(this, "your_assets_json_file.json");
     *
     * @param context  上下文
     * @param fileName assets文件夹下的文件名
     * @return json文件的内容
     */
    public static String readAssetsJsonFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            inputStream = assetManager.open(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }


    /**
     * 插入文件第一行(推荐)
     *
     * @param content 要插入的内容
     * @throws IOException
     */
    public static void insertAtBeginning(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            byte[] originalContent = new byte[(int) raf.length()];
            raf.read(originalContent);
            raf.seek(0);
            raf.write((content + "\n").getBytes());
            raf.write(originalContent);
        }
    }

    /**
     * 插入文件第一行 推荐使用 insertAtBeginning
     */
    @Deprecated(message = "推荐使用 insertAtBeginning 方法")
    public static void writeFileToFirstLine(String filePath, String content) {
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = null;
        try {
            // 读取原始文件内容
            byte[] originalContent = new byte[(int) file.length()];
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.readFully(originalContent);

            // 将文件指针移到开头
            randomAccessFile.seek(0);

            // 写入新内容
            randomAccessFile.write(content.getBytes());

            // 写入原始内容
            randomAccessFile.write(originalContent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readFileBit(String filePath, IFileReadCallBack iCaliFileReadCallBackBack) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            // 创建FileInputStream对象并指定要读取的MP4文件路径
            fis = new FileInputStream(filePath);

            // 创建ByteArrayOutputStream对象
            baos = new ByteArrayOutputStream();

            // 读取文件内容到ByteArrayOutputStream中
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (iCaliFileReadCallBackBack != null) {
                    iCaliFileReadCallBackBack.read(buffer);
                }
                baos.write(buffer, 0, bytesRead);
            }

            // 将MP4文件内容保存在字节数组中
            byte[] mp4Data = baos.toByteArray();

            // 打印字节数组长度
            System.out.println("MP4文件长度：" + mp4Data.length + " 字节");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通用回调方法
     */
    public interface IFileReadCallBack {

        void read(byte[] buffer);
    }

    interface IReadByte {
        void read(byte[] buffer);
    }
}