package cn.mvp.chat1;


import android.util.Log;

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

/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
 * implementation 'org.java-websocket:Java-WebSocket:1.5.3'
 * <dependency>
 * <groupId>org.java-websocket</groupId>
 * <artifactId>Java-WebSocket</artifactId>
 * <version>1.5.3</version>
 * </dependency>
 */
public class ClipboardWebSocketClient {
    private static final int BUFFER_SIZE = 1024 * 1024; // 1 MB
    private static ClipboardWebSocketClient instance;
    private WebSocketClient webSocketClient;
    private IReceiver iReceiver;

    private ClipboardWebSocketClient() {

    }

    public static ClipboardWebSocketClient getInstance() {
        if (instance == null) {
            synchronized (ClipboardWebSocketClient.class) {
                if (instance == null) {
                    instance = new ClipboardWebSocketClient();
                }
            }
        }
        return instance;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void close() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    /**
     * @param ip        192.144.219.245:8885
     * @param iReceiver 接收消息
     */
    public void connService(String ip, IReceiver iReceiver) {
        close();
        this.iReceiver = iReceiver;
        URI serverURI = URI.create("ws://" + ip);//"ws://IP地址:端口号"
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
                Log.i("调试信息", "onMessage:  " + message);
                iReceiver.onReceiverMsg(message);
//                if (msg.getMsgContent().startsWith("cmd_clipboard_set")) {//pc给手机设置剪切板
//                    ClipboardUtils.copyText(msg.getMsgContent().replace("cmd_clipboard_set", ""));
//                    ToastUtils.show("已复制pc剪贴板");
//                } else if (msg.getMsgContent().startsWith("cmd_clipboard_get")) {//获取手机剪切板给pc
//                    sendMsg("cmd_clipboard_get" + ClipboardUtils.getText().toString());
//                    ToastUtils.show("已设置pc剪贴板");
//                }
            }

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
        webSocketClient.send(msg);
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

    public boolean sendMsg(WebSocketChatMsg msg) {
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
                WebSocketChatMsg fileMsg = new WebSocketChatMsg(file.getName(), md5, file.length(), dataToSend);
                sendMsg(fileMsg);
            }
            sendMsg("接收完毕: " + file.getName());

            System.out.println("File send: " + filePath);
        } catch (IOException e) {
            sendMsg("接收文件异常: " + e.getMessage());
            System.err.println("Error sending file: " + e.getMessage());
        }
    }

    public interface IReceiver {
        default void onOpen() {

        }

        void onReceiverMsg(String msg);

        default void onReceiverMsg(ByteBuffer bytes) {

        }

        void onErr(Exception e);

        void log(String log);

        void progress(String msg, int currPrs, WebSocketChatMsg fileInfo);
    }


}