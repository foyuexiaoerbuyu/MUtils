package cn.mvp.chat;

public interface IReceiveListener {
    void receiveData(String content);

    void err(Exception e);
}
