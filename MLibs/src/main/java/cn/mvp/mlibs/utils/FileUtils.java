package cn.mvp.mlibs.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;

import cn.mvp.mlibs.log.XLogUtil;

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
//            throw new RuntimeException("IOException", e);
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
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(fileWriter);
        }
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        } finally {
            IOUtils.close(o);
            IOUtils.close(is);
        }
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
            throw new NullPointerException("无效的文件路径");
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
}