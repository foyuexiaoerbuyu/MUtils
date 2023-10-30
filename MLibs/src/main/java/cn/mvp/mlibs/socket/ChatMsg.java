package cn.mvp.mlibs.socket;


import cn.mvp.mlibs.utils.GsonUtil;

public class ChatMsg {
    private long id;
    private int msgType;//0:文本消息 1:文件
    private long progress;
    private long fileSize;
    private String fileName;
    private String md5;
    private String msgContent;
    private String extra;
    private byte[] fileData;
    private Object extraObj;

    public ChatMsg(String msgContent) {
        this.id = System.currentTimeMillis();
        this.msgContent = msgContent;
    }

    public ChatMsg(String fileName, String md5, long fileSize, byte[] fileData) {
        this.id = System.currentTimeMillis();
        this.msgType = ChatMsgType.MSG_TYPE_FILE;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.md5 = md5;
        this.fileData = fileData;
    }

    public boolean isFile() {
        return msgType == ChatMsgType.MSG_TYPE_FILE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public Object getExtraObj() {
        return extraObj;
    }

    public void setExtraObj(Object extraObj) {
        this.extraObj = extraObj;
    }

    public String toJson() {
        return GsonUtil.toJson(this);
    }

}
