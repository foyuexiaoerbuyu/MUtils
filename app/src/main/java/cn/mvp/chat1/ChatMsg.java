package cn.mvp.chat1;

public class ChatMsg {
    private long id;
    private int msgType;//0:文本 1:文件
    private String fileName;
    private String md5;
    private String msgContent;
    private byte[] fileData;

    public ChatMsg(String msgContent) {
        this.id = System.currentTimeMillis();
        this.msgContent = msgContent;
    }

    public ChatMsg(String fileName, String md5, byte[] fileData) {
        this.id = System.currentTimeMillis();
        this.msgType = 1;
        this.fileName = fileName;
        this.md5 = md5;
        this.fileData = fileData;
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
}
