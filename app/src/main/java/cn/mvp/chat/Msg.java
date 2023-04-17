package cn.mvp.chat;

public class Msg {
    private String contentStr;
    private String filePath;

    public Msg(String contentStr, String filePath) {
        this.contentStr = contentStr;
        this.filePath = filePath;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
