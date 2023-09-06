package cn.mvp.mlibs.weight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * https://www.runoob.com/java/net-serversocket-socket.html
 */

public class SocketUtils {

    private final IServiceNotifyMsg iServiceNotifyMsg;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private BufferedWriter clientWriter;
    private BufferedReader clientReader;
    private BufferedWriter serviceWriter;
    private BufferedReader serviceReader;

    public SocketUtils(IServiceNotifyMsg iServiceNotifyMsg) {
        this.iServiceNotifyMsg = iServiceNotifyMsg;
    }

    /**
     * 启动服务端
     *
     * @param port        服务器端口
     * @param serviceCall 接收客户端消息回调
     */
    public void startService(int port, IReceiverMsg serviceCall) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.printf("启动服务 %d\n", serverSocket.getLocalPort());

                while (true) {
                    Socket serviceSocket = serverSocket.accept();
                    serviceWriter = new BufferedWriter(new OutputStreamWriter(serviceSocket.getOutputStream()));
                    serviceReader = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));

                    receiverMsg(serviceCall, serviceReader);

                    serviceSocket.close();
                }
            } catch (IOException e) {
                iServiceNotifyMsg.errMsg(e, "启动服务器失败");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 客户端连接服务器
     *
     * @param host       服务器地址
     * @param port       端口
     * @param clientCall 接收服务端消息
     */
    public void connService(String host, int port, IReceiverMsg clientCall) {
        try {
            clientSocket = new Socket(host, port);
            clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            clientWriter.write("连接服务器成功");
            clientWriter.newLine();
            clientWriter.flush();

            receiverMsg(clientCall, clientReader);
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "连接服务器异常");
            e.printStackTrace();
        }
    }

    /**
     * @param msg 服务端发消息给客户端
     */
    public void serviceSendMsg(String msg) {
        if (serviceWriter == null) {
            iServiceNotifyMsg.errMsg(new RuntimeException("没起服务器"), "没起服务器");
            System.out.println("没起服务器");
            return;
        }
        try {
            serviceWriter.write(msg);
            serviceWriter.newLine();
            serviceWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            iServiceNotifyMsg.errMsg(e, "服务端发送消息异常");
        }
    }

    /**
     * @param msg 客户端发消息给服务端
     */
    public void clientSendMsg(String msg) {
        try {
            clientWriter.write(msg);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "客户端发送消息异常");
            e.printStackTrace();
        }
    }

    /**
     * @param clientCall 消息回调
     * @param reader     输入流
     */
    private void receiverMsg(IReceiverMsg clientCall, BufferedReader reader) {
        String msg;
        try {
            while ((msg = reader.readLine()) != null) {
                clientCall.receiverMsg(msg);
                if ("Bye".equals(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "接收消息异常");
            e.printStackTrace();
        }
    }

    /**
     * 停止服务器
     */
    public void stopService() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "停止失败");
            e.printStackTrace();
        }
    }

    /**
     * 停止服务器
     */
    public void stopClient() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "停止客户端失败");
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface IReceiverMsg {
        void receiverMsg(String receiveMsg);
    }

    @FunctionalInterface
    public interface IServiceNotifyMsg {
        void errMsg(Exception e, String errMsg);
    }

}
