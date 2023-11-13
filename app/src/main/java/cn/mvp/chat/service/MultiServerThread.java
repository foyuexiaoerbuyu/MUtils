package cn.mvp.chat.service;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class MultiServerThread extends Thread {
    private Socket s;
    private InetAddress inetAddress;
    private List<Socket> listSockets;

    public MultiServerThread(Socket s, InetAddress inetAddress, List<Socket> listSockets) {
        this.s = s;
        this.inetAddress = inetAddress;
        this.listSockets = listSockets;
    }

    public void BroadCast(Socket s, byte[] by, int res) {
        // 将服务器接收到的消息发送给除了发送方以外的其他客户端
        int i = 0;
        for (Socket socket : listSockets) {
            if (s != socket)  // 判断不是当前发送的客户端
            {
                System.out.println("发送给用户: " + listSockets.indexOf(socket));
                BufferedOutputStream ps = null;
                try {
                    ps = new BufferedOutputStream(socket.getOutputStream());
                    ps.write(by, 0, res);   // 写入输出流，将内容发送给客户端的输入流
                    ps.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 服务器与客户端的交互线程
    @Override
    public void run() {
        BufferedInputStream ois = null;
        BufferedOutputStream oos = null;
        try {
            ois = new BufferedInputStream(s.getInputStream());

            oos = new BufferedOutputStream(s.getOutputStream());

            int i = 0;
            while (true) {
                //System.out.println("进入MultiChatServerThread");
                byte[] by = new byte[1024 + 2];
                //System.out.println("by.length: " + by.length);
                int res = 0;
                res = ois.read(by);
                // 对读取到的字节数组第一位位置进行修改，标识该数据流是由哪个用户发送来的
                by[0] = (byte) listSockets.indexOf(s);

                if (by[1] == 2) {
                    // 因为前两个位置是标志位，所以length的大小为读取的字节数-2,同时offset也从第三个位置(下标是2)开始读
                    String receive = new String(by, 2, res - 2);

                    if (receive.equalsIgnoreCase("bye")) {
                        // 如果客户端发送的是bye, 说明其下线，则从listSockets里删除对应的socket.
                        oos.write(receive.getBytes());  // 把bye给客户端的读取线程，从而可以关闭掉读取线程
                        oos.flush();
                        System.out.printf("用户%d下线, ", listSockets.indexOf(s));
                        listSockets.remove(s);
                        System.out.printf("目前聊天室仍有%d人\n", listSockets.size());
                    }
                }
                System.out.println("i" + i + "res = " + res);
                System.out.println("by.length: " + by.length);
                System.out.println("Socket[]: " + Arrays.toString(listSockets.toArray()));
                // 调用函数，将接受到的消息发送给所有客户端
                BroadCast(s, by, res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}