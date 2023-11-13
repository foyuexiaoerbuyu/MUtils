package cn.mvp.chat.service;



import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiServer {
    public static void main(String[] args) {
        ServerSocket ss = null;
        Socket s = null;

        // 定义一个List列表来保存每个客户端，每新建一个客户端连接，就添加到List列表里。
        List<Socket> listSocket = new ArrayList<>();
        try {
            // 1. 创建ServerSocket类型的对象并提供端口号
            ss = new ServerSocket(3243);
            // 2. 等待客户端的连接请求，调用accept方法
            // 采用多线程的方式，允许多个用户请求连接。
            int i = 0;
            while (true) {
                System.out.println("等待客户端的连接请求...");
                s = ss.accept();
                listSocket.add(s);
                //sArr[i] = s;
                i++;
                System.out.printf("欢迎用户%d加入群聊!\n", i);
                System.out.printf("目前群聊中共有%d人\n", listSocket.size());
                InetAddress inetAddress = s.getInetAddress();
                System.out.println("客户端" + inetAddress + "连接成功!");
                // 调用多线程方法，每一个连上的客户端，服务器都有一个线程为之服务
                new MultiServerThread(s, inetAddress, listSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

