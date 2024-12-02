package cn.mvp.mlibs.utils;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * webSocket 日志工具类
 * okhttp.WebSocket with message queue
 */
public class WSLogUtils {

    private static final String TAG = "WebSocketSingleton";
    private static volatile WSLogUtils instance;
    private OkHttpClient client;
    private WebSocket webSocket;
    private OnMessageReceivedListener onMessageReceivedListener;
    private BlockingQueue<Runnable> messageQueue; // 消息队列
    private Thread sendMessageThread; // 发送消息的线程

    // 接收消息的回调接口
    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);

        void onBinaryMessageReceived(ByteString bytes);

        void onClosed(int code, String reason);

        void onFailure(Throwable t, Response response);
    }

    // 私有构造函数
    private WSLogUtils() {
        client = new OkHttpClient();
        messageQueue = new LinkedBlockingQueue<>();
        // 创建并启动发送消息的线程
        sendMessageThread = new Thread(new MessageSender());
        sendMessageThread.start();
    }

    // 获取单例实例
    public static WSLogUtils getInstance() {
        if (instance == null) {
            synchronized (WSLogUtils.class) {
                if (instance == null) {
                    instance = new WSLogUtils();
                }
            }
        }
        return instance;
    }

    // 连接到WebSocket服务器
    public void connect(String url, OnMessageReceivedListener listener) {
        this.onMessageReceivedListener = listener;
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "onOpen: " + response.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if (onMessageReceivedListener != null) {
                    onMessageReceivedListener.onMessageReceived(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                if (onMessageReceivedListener != null) {
                    onMessageReceivedListener.onBinaryMessageReceived(bytes);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "onClosing: " + code + " " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "onClosed: " + code + " " + reason);
                if (onMessageReceivedListener != null) {
                    onMessageReceivedListener.onClosed(code, reason);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "onFailure: ", t);
                if (onMessageReceivedListener != null) {
                    onMessageReceivedListener.onFailure(t, response);
                }
            }
        });
    }

    // 发送文本消息
    public void sendText(final String message) {
        Runnable task = () -> {
            if (webSocket != null) {
                webSocket.send(message);
            } else {
                Log.w(TAG, "WebSocket is not connected.");
            }
        };
        try {
            messageQueue.put(task); // 将任务放入队列
        } catch (InterruptedException e) {
            Log.e(TAG, "sendText interrupted", e);
        }
    }

    // 发送二进制消息
    public void sendBytes(final ByteString bytes) {
        Runnable task = () -> {
            if (webSocket != null) {
                webSocket.send(bytes);
            } else {
                Log.w(TAG, "WebSocket is not connected.");
            }
        };
        try {
            messageQueue.put(task); // 将任务放入队列
        } catch (InterruptedException e) {
            Log.e(TAG, "sendBytes interrupted", e);
        }
    }

    // 关闭WebSocket连接
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
        }
        // 停止发送消息的线程
        sendMessageThread.interrupt();
    }

    // 发送消息的线程
    private class MessageSender implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable task = messageQueue.take(); // 从队列中取出任务
                    task.run(); // 执行任务
                } catch (InterruptedException e) {
                    Log.e(TAG, "MessageSender interrupted", e);
                    break; // 如果中断，则退出循环
                }
            }
        }
    }
}