package cn.mvp.chat1;


import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * https://blog.huangyuanlove.com/2017/12/25/Android%E4%B8%AD%E4%BD%BF%E7%94%A8WebSocket/
 * implementation 'org.java-websocket:Java-WebSocket:1.5.3'
 * <dependency>
 * <groupId>org.java-websocket</groupId>
 * <artifactId>Java-WebSocket</artifactId>
 * <version>1.5.3</version>
 * </dependency>
 */
public class ChatWebSocket extends WebSocketServer {
    public ChatWebSocket(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public ChatWebSocket(InetSocketAddress address) {
        super(address);
    }

    public static void main(String[] args) {
//        WebSocketImpl.DEBUG = true;
        try {
            int port = 8887; // 843 flash policy port
            ChatWebSocket s = new ChatWebSocket(port);

            s.start();
            System.out.println("ChatServer started on port: " + s.getPort());

            BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String in = sysin.readLine();
                s.broadcast(in);
                if (in.equals("exit")) {
                    s.stop(1000);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake clientHandshake) {

        broadcast("new connection: " + clientHandshake.getResourceDescriptor());
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {
        broadcast(webSocket + " onClose");
        System.out.println(webSocket + " onClose");
    }

    @Override
    public void onMessage(org.java_websocket.WebSocket webSocket, String s) {

        broadcast(s);
        System.out.println(webSocket + ": " + s);
    }

    @Override
    public void onError(org.java_websocket.WebSocket webSocket, Exception e) {
        e.printStackTrace();
        if (webSocket != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
    }
}