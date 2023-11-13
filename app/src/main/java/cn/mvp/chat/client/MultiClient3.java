package cn.mvp.chat.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MultiClient3 extends Thread {
    private Socket ss;

    public MultiClient3() {
    }

    public MultiClient3(Socket ss) {
        this.ss = ss;
    }

    public byte[] reviseArr(byte[] by, int res) {
        byte[] newByteArr = new byte[by.length + 2];
        // 将by字节数组的内容都往后移动两位，即头部的两个位置空出来作为标志位
        for (int i = 0; i < by.length; i++) {
            newByteArr[i + 2] = by[i];
        }
        return newByteArr;
    }

    @Override
    public void run() {
        BufferedInputStream bis = null;
        BufferedOutputStream bosFile = null;    // 与输出文件流相关联
        try {
            bis = new BufferedInputStream(ss.getInputStream());
            // 等待接收服务器发送回来的消息
            while (true) {
                byte[] by = new byte[1024 + 2];
                int res = bis.read(by);
                int sendUser = by[0];
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                String format = sdf.format(date);
                if (by[1] == 1) // 说明传的是文件
                {
                    bosFile = new BufferedOutputStream(new FileOutputStream("./directoryTest/用户" + sendUser + "-传输的文件.png", true));
                    bosFile.write(by, 2, res - 2);
                    bosFile.flush();
                    if (res < 1026)   // 说明是最后一次在传送文件，所以传送的字节数才会小于字节数组by的大小
                    {
                        //System.out.println("客户端接收到的信息" + receive);
                        System.out.println("用户" + sendUser + "\t" + format + ":");
                        System.out.printf("用户%d发送的文件传输完成\n", sendUser);
                    }
                } else    // 说明传输的是聊天内容，则按字符串的形式进行解析
                {
                    // 利用String构造方法的形式，将字节数组转化成字符串打印出来
                    String receive = new String(by, 2, res);
                    System.out.println("用户" + sendUser + "\t" + format + ":");
                    System.out.println(receive);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // 主线程写操作
        //MultiClient mc = new MultiClient();
        Socket ss = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;     // 与文件关联的流
        MultiClient3 mcc = null;
        try {
            ss = new Socket("127.0.0.1", 3243);
            System.out.println("服务器连接成功");
            System.out.println("-----------聊天室-----------");
            bos = new BufferedOutputStream(ss.getOutputStream());
            Scanner sc = new Scanner(System.in);
            mcc = new MultiClient3(ss);
            mcc.start();
            byte[] by = new byte[1024];
            int res = 0;
            int i = 0;
            while (true) {
                String str = sc.nextLine();
                if (str.equals("传输文件")) {
                    bis = new BufferedInputStream(new FileInputStream("./directoryTest/壁纸1.png"));
                    while ((res = bis.read(by)) != -1) {
                        byte[] newByteArr = mcc.reviseArr(by, res);
                        ;
                        newByteArr[1] = 1;  // 表示第二个位置上的值为1时表示传输的是文件
                        bos.write(newByteArr, 0, res + 2);
                        bos.flush();

                    }
                } else {
                    byte[] sb = str.getBytes(); // 转化为字节数组
                    byte[] newByteArr = mcc.reviseArr(sb, sb.length);
                    newByteArr[1] = 2;      // 表示第二个位置上的值为2时表示传输的是聊天内容
                    bos.write(newByteArr);  // 把内容发给服务器
                    bos.flush();
                    // 如果用户输入bye则表示用户下线
                    if (str.equalsIgnoreCase("bye")) {
                        System.out.println("用户下线!");
                        break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mcc.stop();
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}