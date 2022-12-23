package cn.mvp.mlibs.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @program: website
 * @description: 转储压缩文件
 * @author: smallsoup
 * @create: 2018-08-12 17:58
 **/

public class ZipFile {

    private final String TAG = "ZipFile";

//    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFile.class);

    /**
     * 格式化文件名格式
     */
    private static final String AUDIT_LOG_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 压缩后文件后缀
     */
    private static final String AUDIT_FILE_ZIP_SUFFIX = ".zip";

    /**
     * 压缩前文件后缀
     */
    private static final String AUDIT_FILE_EXT = ".log";

    private static final int ZIP_BUFFER = 4096;

    /**
     * 控制压缩后的文件解压后是否带base路径
     */
    private static final String rootPath = "";


    public static void main(String[] args) throws IOException {

        System.out.println();

        new ZipFile().zipAuditLogFile("D:/tmp/successLog/logs/root.log");
    }

    /**
     * 日志压缩
     *
     * @param waitZipFile 要压缩文件名
     * @throws IOException
     */
    private void zipAuditLogFile(String waitZipFile) throws IOException {
        File oldFile = new File(waitZipFile);

        if (!oldFile.exists()) {
            Log.e(TAG, "zipAuditLogFile name is " + waitZipFile + " not exist");
            return;
        }

        //生成zip文件名
        DateFormat dataFormat = new SimpleDateFormat(AUDIT_LOG_FORMAT);
        String formatTime = dataFormat.format(oldFile.lastModified());

        int end = waitZipFile.length() - AUDIT_FILE_EXT.length();
        String zipFileName = waitZipFile.subSequence(0, end) + "_" + formatTime + AUDIT_FILE_ZIP_SUFFIX;

        File zipFile = new File(zipFileName);

        FileOutputStream zipfos = null;
        ZipOutputStream zipOs = null;
        CheckedOutputStream cos = null;


        try {
            zipfos = new FileOutputStream(zipFile);
            cos = new CheckedOutputStream(zipfos, new CRC32());

            zipOs = new ZipOutputStream(cos);

            compress(oldFile, zipOs, rootPath);

            if (zipFile.exists()) {
                // 写完的日志文件权限改为400
                try {
                    //linux上才可以运行,windows上需要装cygwin并且把cygwin的bin目录加到环境变量的path中才可以
                    Runtime.getRuntime().exec("chmod 400 -R " + zipFile);
                    //压缩后删除旧文件
                    boolean isDelete = oldFile.delete();
                    //创建新文件
                    if (isDelete) {
                        oldFile.createNewFile();
                    }
//                    boolean isSuccess = PathUtil.setFilePermision(zipFile.toPath(), ARCHIVE_LOGFILE_PERMISION);
//                    LOGGER.warn("set archive file: {}, permision result is {}", zipFile.getAbsolutePath(), isSuccess);
                } catch (IOException e) {
                    Log.e(TAG, "set archive file:" + zipFile + " permision catch an error: " + e);
                }
            }

        } finally {

            if (null != zipOs) {
                zipOs.close();
            }

            if (null != cos) {
                cos.close();
            }

            if (null != zipfos) {
                zipfos.close();
            }
        }
    }

    /**
     * 压缩文件或目录
     *
     * @param oldFile 要压缩的文件
     * @param zipOut  压缩文件流
     * @param baseDir baseDir
     * @throws IOException
     */
    private void compress(File oldFile, ZipOutputStream zipOut, String baseDir) throws IOException {

        if (oldFile.isDirectory()) {

            compressDirectory(oldFile, zipOut, baseDir);

        } else {
            compressFile(oldFile, zipOut, baseDir);
        }
    }

    /**
     * 压缩目录
     *
     * @param dir     要压缩的目录
     * @param zipOut  压缩文件流
     * @param baseDir baseDir
     * @throws IOException
     */
    private void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException {

        File[] files = dir.listFiles();

        for (File file : files) {
            compress(file, zipOut, baseDir + dir.getName() + File.separator);
        }
    }

    /**
     * 压缩文件
     *
     * @param oldFile 要压缩的文件
     * @param zipOut  压缩文件流
     * @param baseDir baseDir
     * @throws IOException
     */
    private void compressFile(File oldFile, ZipOutputStream zipOut, String baseDir) throws IOException {

        if (!oldFile.exists()) {
            Log.e(TAG, "zipAuditLogFile name is " + oldFile + " not exist");
            return;
        }

        BufferedInputStream bis = null;

        try {

            bis = new BufferedInputStream(new FileInputStream(oldFile));

            ZipEntry zipEntry = new ZipEntry(baseDir + oldFile.getName());

            zipOut.putNextEntry(zipEntry);

            int count;

            byte data[] = new byte[ZIP_BUFFER];

            while ((count = bis.read(data, 0, ZIP_BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }

        } finally {
            if (null != bis) {
                bis.close();
            }
        }

    }

}