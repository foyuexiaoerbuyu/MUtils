package cn.mvp.mlibs.utils.ents;

import java.nio.file.attribute.FileTime;

/**
 * 文件基本信息
 */
public class FileInfos {
        /** File name */
        private String fileName;
        /** File absolute path */
        private String fileAbsolutePath;
        /** File size */
        private long fileSize;
        /** Is file */
        private boolean isFile;
        /** Is directory */
        private boolean isDirectory;
        /** Is hidden */
        private boolean isHidden;
        /** Last modified */
        private long lastModified;

        /** Creation time */
        private FileTime fileTime;
        /** Last access time */
        private FileTime fileTime2;
        /** Last modified time */
        private FileTime fileTime1;
        /** Is directory */
        private boolean directory;
        /** Is regular file */
        private boolean regularFile;
        /** Is symbolic link */
        private boolean symbolicLink;
        /** Is other */
        private boolean other;
        /** Size 单位:bytes */
        private long size;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileAbsolutePath() {
            return fileAbsolutePath;
        }

        public void setFileAbsolutePath(String fileAbsolutePath) {
            this.fileAbsolutePath = fileAbsolutePath;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public boolean isFile() {
            return isFile;
        }

        public void setFile(boolean file) {
            isFile = file;
        }

        public boolean isDirectory() {
            return isDirectory;
        }

        public void setDirectory(boolean directory) {
            isDirectory = directory;
        }

        public boolean isRegularFile() {
            return regularFile;
        }

        public void setRegularFile(boolean regularFile) {
            this.regularFile = regularFile;
        }

        public boolean isSymbolicLink() {
            return symbolicLink;
        }

        public void setSymbolicLink(boolean symbolicLink) {
            this.symbolicLink = symbolicLink;
        }

        public boolean isOther() {
            return other;
        }

        public void setOther(boolean other) {
            this.other = other;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public boolean isHidden() {
            return isHidden;
        }

        public void setHidden(boolean hidden) {
            isHidden = hidden;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public FileTime getFileTime() {
            return fileTime;
        }

        public void setFileTime(FileTime fileTime) {
            this.fileTime = fileTime;
        }

        public FileTime getFileTime2() {
            return fileTime2;
        }

        public void setFileTime2(FileTime fileTime2) {
            this.fileTime2 = fileTime2;
        }

        public FileTime getFileTime1() {
            return fileTime1;
        }

        public void setFileTime1(FileTime fileTime1) {
            this.fileTime1 = fileTime1;
        }
    }