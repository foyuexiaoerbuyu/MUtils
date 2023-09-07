package cn.mvp.chat1;


import android.os.Environment;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
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

    public void connService(String ip, IReceiver iReceiver) {
        close();
        this.iReceiver = iReceiver;
        URI serverURI = URI.create("ws://" + ip);//"ws://172.31.254.234:8887"
        Map<String, String> headers = new HashMap<>();

        headers.put("client_name", DeviceUtils.getDeviceModel() + "_" + DeviceUtils.getManufacturer());//连接名
        webSocketClient = new WebSocketClient(serverURI, headers) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                StringBuffer sb = new StringBuffer();
                sb.append("onOpen at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("服务器状态：");
                sb.append(handshakedata.getHttpStatusMessage());
                sb.append("\n");
                Log.i("调试信息", "onOpen:  " + sb);
                iReceiver.log(sb.toString());
            }

            @Override
            public void onMessage(String message) {
                ChatMsg msg = GsonUtil.fromJson(message, ChatMsg.class);
                if (msg.getMsgType() == 0) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("\n服务端返回数据：").append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL)).append("\n");
                    sb.append(msg.getMsgContent());
                    iReceiver.log(sb.toString());
                } else {
                    File directory = SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String name = directory + File.separator + msg.getFileName();
                    Log.i("调试信息", "文件名:  " + name);
                    FileUtils.writeFile(name, msg.getFileData(), true);
                    Log.i("调试信息", "File received and saved!");
                }
                Log.i("调试信息", "onMessage:  " + message);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                super.onMessage(bytes);

                File directory = SDCardUtils.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String name = directory + "/largefile.apk";
                Log.i("调试信息", "文件名:  " + name);
                FileUtils.writeFile(name, bytes, true);
                Log.i("调试信息", "File received and saved!");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                StringBuffer sb = new StringBuffer();
                sb.append("\nonClose at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(" 与服务器连接断开: ").append(code);
                sb.append(" 关闭原因: ").append(reason);
                sb.append(remote);
                sb.append("\n");
                iReceiver.log(sb.toString());
                Log.i("调试信息", "onClose:  " + sb);
                if (webSocketClient != null) {
                    webSocketClient.close();
                }

            }

            @Override
            public void onError(Exception ex) {
                StringBuffer sb = new StringBuffer();
                sb.append("onError at time：");
                sb.append(DateUtil.formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL));
                sb.append("\n");
                sb.append(ex);
                sb.append("\n");
                iReceiver.log(sb.toString());
                Log.i("调试信息", "onError:  " + sb);
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

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void close() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public void sendMsg(String msg) {
        if (!webSocketClient.isOpen()) {
            iReceiver.log("正在重连...");
            if (webSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    webSocketClient.connect();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    iReceiver.log("重连失败...");
                }
            } else if (webSocketClient.getReadyState().equals(ReadyState.CLOSING) || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
                webSocketClient.reconnect();
                iReceiver.log("正在重连...");
            }
            return;
        }
//                if (webSocketClient.isClosed() || webSocketClient.isClosing()) {
//                    Toast.makeText(this, "Client正在关闭", Toast.LENGTH_SHORT).show();
//                    webSocketClient.connect();
//                    break;
//                }

        webSocketClient.send(msg);

    }

    public interface IReceiver {
        void onReceiverMsg(String msg);

        void onReceiverMsg(ByteBuffer bytes);

        void onErr(Exception e);

        void log(String log);
    }


}