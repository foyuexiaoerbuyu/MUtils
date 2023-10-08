package cn.mvp.chat1;


import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.ClipboardUtils;
import com.hjq.toast.ToastUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.DeviceUtils;
import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.GsonUtil;
import cn.mvp.mlibs.utils.SDCardUtils;

/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
 * implementation 'org.java-websocket:Java-WebSocket:1.5.3'
 * <dependency>
 * <groupId>org.java-websocket</groupId>
 * <artifactId>Java-WebSocket</artifactId>
 * <version>1.5.3</version>
 * </dependency>
 */
public class ChatWebSocketClient {
    private static ChatWebSocketClient instance;
    private WebSocketClient webSocketClient;
    private IReceiver iReceiver;

    private ChatWebSocketClient() {

    }

    public static ChatWebSocketClient getInstance() {
        if (instance == null) {
            synchronized (ChatWebSocketClient.class) {
                if (instance == null) {
                    instance = new ChatWebSocketClient();
                }
            }
        }
        return instance;
    }

    private static final int BUFFER_SIZE = 1024 * 1024; // 1 MB

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void close() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public void connService(String ip, IReceiver iReceiver) {
        close();
        this.iReceiver = iReceiver;
        URI serverURI = URI.create("ws://" + ip);//"ws://172.31.254.234:8887"
        Map<String, String> headers = new HashMap<>();

        headers.put("client_name", DeviceUtils.getDeviceModel() + "_" + DeviceUtils.getManufacturer());//连接名
        webSocketClient = new WebSocketClient(serverURI, headers) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                // 连接成功后发送消息(主要是为了重连后发送待发送数据)
                String sb = "onOpen at time：" +
                        DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL) +
                        "\n服务器状态：" +
                        serverHandshake.getHttpStatusMessage() +
                        "\n";
                iReceiver.log(sb);
                iReceiver.onOpen();
            }

            @Override
            public void onMessage(String message) {
                // 接收到消息的处理逻辑
                ChatMsg msg = GsonUtil.fromJson(message, ChatMsg.class);
                if (msg.getMsgType() == ChatMsg.MSG_TYPE_MSG) {
                    if (msg.getMsgContent().startsWith("cmd_clipboard_set")) {//pc给手机设置剪切板
                        ClipboardUtils.copyText(msg.getMsgContent().replace("cmd_clipboard_set", ""));
                        ToastUtils.show("已复制pc剪贴板");
                    } else if (msg.getMsgContent().startsWith("cmd_clipboard_get")) {//获取手机剪切板给pc
                        sendMsg("cmd_clipboard_get" + ClipboardUtils.getText().toString());
                        ToastUtils.show("已设置pc剪贴板");
                    } else {
                        String sb = "服务端消息: " + DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL) +
                                "\n   " + msg.getMsgContent() + "\n";
                        iReceiver.onReceiverMsg(sb);
                    }
                } else if (msg.getMsgType() == ChatMsg.MSG_TYPE_CMD) {
                    if (msg.getMsgContent().equals("cm_pull_files")) {
                        String path = SDCardUtils.getExternalPublicStorageDirectory().getPath() + File.separator + "01tmp" + File.separator;
                        FileUtils.readFileNames(path, (filePath, file) -> {
                            // TODO: 2023/10/8 推文件到服务器
                            Log.i("调试信息", "开始发送文件:  " + filePath);
                            instance.sendFileToService(filePath);
                            Log.i("调试信息", "发送文件完毕:  " + filePath);
                        });
                    }
                } else {
                    File directory = SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String name = directory + File.separator + msg.getFileName();
                    String fileMD5 = FileUtils.getFileMD5(name);
                    if (!FileUtils.isFileExist(name)) {
                        iReceiver.onReceiverMsg("收到服务端传来的文件,正在下载...");
                    } else if (directory.isFile() && fileMD5.equals(msg.getMd5())) {
                        FileUtils.deleteFile(name);//存在且文件相同就删除掉
                    }
                    Log.i("调试信息", "文件名:  " + name + "  prs: " + msg.getProgress() + " md5: " + msg.getMd5());
                    FileUtils.writeFile(name, msg.getFileData(), true);
                    int prs = (int) (msg.getProgress() * 100 / msg.getFileSize());
                    if (fileMD5.equals(msg.getMd5())) {
                        iReceiver.progress("接收完毕", 100, msg);
                        iReceiver.onReceiverMsg("接收完毕");
                    } else {
                        iReceiver.progress("正在接收文件...", prs, msg);
                        Log.i("调试信息", "正在接收: " + msg.getProgress() + "  " + msg.getFileSize() + " 接收进度: " + prs);
                    }
                }
//                Log.i("调试信息", "onMessage:  " + message);
            }

//            @Override
//            public void onMessage(ByteBuffer bytes) {
//                super.onMessage(bytes);
//                iReceiver.onReceiverMsg(bytes);
////                SDCardUtils.getExPubDownDir();
////                File directory = SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
////                String name = directory + "/largefile.apk";
////                Log.i("调试信息", "文件名:  " + name);
////                FileUtils.writeFile(name, bytes, true);
////                Log.i("调试信息", "File received and saved!");
//            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                // 连接关闭的处理逻辑
                StringBuffer sb = new StringBuffer();
                sb.append("客户端消息：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(" 与服务器连接断开连接: ").append(code);
                sb.append(" \n关闭原因: ").append(remote ? "服务器已关闭..." : reason);
//                sb.append(remote);
                sb.append("\n");
                iReceiver.log(sb.toString());
                if (webSocketClient != null) {
                    webSocketClient.close();
                }

            }

            @Override
            public void onError(Exception ex) {
                // 错误处理逻辑
                iReceiver.onErr(ex);
                StringBuilder sb = new StringBuilder();
                sb.append("onError at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(ex);
                sb.append("\n");
                iReceiver.log(sb.toString());
            }
        };
        InetSocketAddress localSocketAddress = webSocketClient.getLocalSocketAddress();
        if (localSocketAddress != null) {
            Log.i("调试信息", "connService:  " + localSocketAddress.getHostString());
            Log.i("调试信息", "connService:  " + localSocketAddress.getHostName());
            Log.i("调试信息", "connService:  " + localSocketAddress.getPort());
            Log.i("调试信息", "connService:  " + GsonUtil.toJson(localSocketAddress));
        }
        webSocketClient.connect();
    }

    public boolean sendMsg(String msg) {
        if (reconnection()) return false;
        webSocketClient.send(new ChatMsg(msg).toJson());
        return true;
    }

    public boolean reconnection() {
        if (!webSocketClient.isOpen()) {
            iReceiver.log("正在重连...\n");
            if (webSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    webSocketClient.connect();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    iReceiver.log("重连失败...");
                }
            } else if (webSocketClient.getReadyState().equals(ReadyState.CLOSING) || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
                webSocketClient.reconnect();
                iReceiver.log("正在重连...\n");
            }
            return true;
        }
        return false;
    }

    public interface IReceiver {
        default void onOpen() {

        }

        void onReceiverMsg(String msg);

        default void onReceiverMsg(ByteBuffer bytes) {

        }

        void onErr(Exception e);

        void log(String log);

        void progress(String msg, int currPrs, ChatMsg fileInfo);
    }

    public boolean sendMsg(ChatMsg msg) {
        if (reconnection()) return false;
        webSocketClient.send(msg.toJson());
        return true;
    }

    public void sendFileToService(String filePath) {
        File file = new File(filePath);
        String md5 = FileUtils.getFileMD5(file);
        sendMsg("开始接收文件: " + file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] dataToSend = Arrays.copyOf(buffer, bytesRead);
                ChatMsg fileMsg = new ChatMsg(file.getName(), md5, file.length(), dataToSend);
                sendMsg(fileMsg);
            }
            sendMsg("接收完毕: " + file.getName());

            System.out.println("File send: " + filePath);
        } catch (IOException e) {
            sendMsg("接收文件异常: " + e.getMessage());
            System.err.println("Error sending file: " + e.getMessage());
        }
    }


}