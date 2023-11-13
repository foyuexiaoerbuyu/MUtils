package cn.mvp.chat;

import java.io.IOException;
import java.net.Socket;

public class SocketManager {
    private static final String SERVER_IP = "your_server_ip";
    private static final int SERVER_PORT = 12345;

    private static SocketManager instance;
    private Socket socket;

    private SocketManager() {
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(SERVER_IP, SERVER_PORT);
        }
        return socket;
    }

    public void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}