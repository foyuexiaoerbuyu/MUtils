package cn.mvp.chat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * udp广播服务类服务
 */
public class UdpUtils {

    private static UdpUtils udpServer;
    private boolean isRunning;
    private IUdpReceiveMsgCallBack iUdpServiceReceiveMsgCallBack;
    private DatagramPacket receivePacket;
    private DatagramSocket serverSocket;

    public static UdpUtils getInstance() {
        if (udpServer == null) {
            udpServer = new UdpUtils();
        }
        return udpServer;
    }

    /**
     * @param msg "你好服务端"
     */
    public static void sendToService(String msg, IUdpReceiveMsgCallBack iUdpClientReceiveMsgCallBack) {
        new Thread(() -> {
            try {
                // 创建UDP socket
                DatagramSocket clientSocket = new DatagramSocket();

                // 设置socket为广播模式
                clientSocket.setBroadcast(true);

                // 广播地址
                InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                int serverPort = 8888;

                // 发送广播消息
                byte[] sendData = msg.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, serverPort);
                clientSocket.send(sendPacket);
                // 接收服务器回复
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.setSoTimeout(5000); // 设置超时时间为5秒
                clientSocket.receive(receivePacket);
                String receiveMsg = new String(receivePacket.getData()).trim();
                iUdpClientReceiveMsgCallBack.receiveMsg(receiveMsg);
                System.out.println("Received response from server at " + receivePacket.getAddress() + ": " + receiveMsg);

                // 关闭socket连接
                clientSocket.close();
            } catch (Exception e) {
                iUdpClientReceiveMsgCallBack.onErr(e);
                System.out.println("No servers found.");
            }
        }).start();
    }

    /**
     * @param iUdpServiceReceiveMsgCallBack 接收客户端消息广播回调
     */
    public void startService(IUdpReceiveMsgCallBack iUdpServiceReceiveMsgCallBack) {
        this.iUdpServiceReceiveMsgCallBack = iUdpServiceReceiveMsgCallBack;
        new Thread(() -> {
            isRunning = true;
            // 创建UDP socket
            try {
                serverSocket = new DatagramSocket(8888);

                byte[] receiveData = new byte[1024];
                byte[] sendData;
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                iUdpServiceReceiveMsgCallBack.serviceLog("启动Udp服务器成功,正在接收消息");
                while (isRunning) {
                    // 接收广播消息
                    serverSocket.receive(receivePacket);

                    String message = new String(receivePacket.getData()).trim();
                    System.out.println("Received broadcast message from " + receivePacket.getAddress() + ": " + message);
                    iUdpServiceReceiveMsgCallBack.receiveMsg(message);

//                    // 回复消息给客户端(应该在上面的回调里发消息然后关闭,不然可扩展性太差)
//                    sendMsgToClient(connSuccessMsg);
                }
            } catch (Exception e) {
                iUdpServiceReceiveMsgCallBack.onErr(e);
                e.printStackTrace();
            }
        }).start();
    }

    public void closeService() {
        isRunning = false;
        serverSocket.close();
        iUdpServiceReceiveMsgCallBack.serviceLog("udp服务器已关闭");
    }

    public void sendMsgToClient(String msg) {
        try {
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            byte[] sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            iUdpServiceReceiveMsgCallBack.onErr(e);
            iUdpServiceReceiveMsgCallBack.serviceLog("发送消息给客户端失败: " + e.getLocalizedMessage());
        }
    }

    public interface IUdpReceiveMsgCallBack {

        void receiveMsg(String receiveMsg);

        void onErr(Exception e);

        /**
         * 只有服务端有用到
         *
         * @param msg 服务端输出的日志
         */
        void serviceLog(String msg);
    }

}