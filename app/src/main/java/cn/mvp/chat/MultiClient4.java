package cn.mvp.chat;


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MultiClient4 extends Thread {
    private Socket ss;
    private Msg mMsg;
    private IReceiveListener mIReceiveListener;


    BufferedOutputStream bos = null;


    private MultiClient4() {
    }

    public MultiClient4(IReceiveListener iReceiveListener) throws IOException {
        mIReceiveListener = iReceiveListener;
        try {
            this.ss = new Socket("172.31.254.234", 3243);
            Log.i("调试信息", "服务器连接成功");
            Log.i("调试信息", "-----------聊天室-----------");
        } catch (Exception e) {
            e.printStackTrace();
            mIReceiveListener.err(e);
            Log.e("调试信息", "MultiClient4  ", e);
            Log.e("调试信息", e.getLocalizedMessage());
            Log.i("调试信息", "e = 服务器连接失败 请检查是否在一个局域网");
        }
        start();
    }

    public byte[] reviseArr(byte[] by, int res) {
        byte[] newByteArr = new byte[by.length + 2];
        // 将by字节数组的内容都往后移动两位，即头部的两个位置空出来作为标志位
        for (int i = 0; i < by.length; i++) {
            newByteArr[i + 2] = by[i];
        }
        return newByteArr;
    }

    // 子线程执行读操作，读取服务端发回的数据
    @Override
    public void run() {

        BufferedInputStream bis = null;
        BufferedOutputStream bosFile = null;    // 与输出文件流相关联
        try {
            bis = new BufferedInputStream(ss.getInputStream());
            //bosFile = new BufferedOutputStream(new FileOutputStream("./directoryTest/src/用户1 IO流的框架图.png"));
            // 等待接收服务器发送回来的消息
            while (true) {
                Log.i("调试信息", "bosFile = 一直在循环");
                byte[] by = new byte[1024 + 2];
                int res = bis.read(by);
                int sendUser = by[0];
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                String format = sdf.format(date);
                if (by[1] == 1) // 说明传的是文件
                {
                    //String filePath = String.format("./directoryTest/src/用户%d传送来的IO流的框架图.png", sendUser);
                    bosFile = new BufferedOutputStream(new FileOutputStream("./directoryTest/用户" + sendUser + "-传输的文件.png", true));
                    bosFile.write(by, 2, res - 2);
                    bosFile.flush();
                    if (res < 1026)   // 说明是最后一次在传送文件，所以传送的字节数才会小于字节数组by的大小
                    {
                        Log.i("调试信息", "用户" + sendUser + "\t" + format + ":");
                        System.out.printf("用户%d发送的文件传输完成\n", sendUser);
                        if (mIReceiveListener != null)
                            mIReceiveListener.receiveData("传输文件完毕");
                    }
                } else    // 说明传输的是聊天内容，则按字符串的形式进行解析
                {
                    // 利用String构造方法的形式，将字节数组转化成字符串打印出来
                    String receive = new String(by, 2, res);
                    Log.i("调试信息", "用户" + sendUser + "\t" + format + ":");
                    Log.i("调试信息", receive);
                    if (mIReceiveListener != null)
                        mIReceiveListener.receiveData("接收到的数据字符串: " + receive);
                }
            }
        } catch (Exception e) {
            mIReceiveListener.err(e);
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
            } catch (Exception e) {
                mIReceiveListener.err(e);
                e.printStackTrace();
            }
        }
    }

    private void setReceiveListenernew(IReceiveListener iReceiveListener) {
        mIReceiveListener = iReceiveListener;
    }

    public void sendMsg(Msg msg) throws IOException {
        mMsg = msg;
        Scanner sc = new Scanner(System.in);
        Log.i("调试信息", " 启动线程 ");
        byte[] by = new byte[1024];
        int res = 0;
        bos = new BufferedOutputStream(ss.getOutputStream());

//        while (true) {
        // 由用户输入选择执行不同的传输任务
        // 若用户输入传输文件，则传输指定文件，否则，则正常聊天任务
        Log.i("调试信息", " 开始循环");
//            String str = sc.nextLine();
        String str = msg.getContentStr();
        Log.i("调试信息", " 输入信息 " + str);
        if (str.equals("传输文件")) {
            // 与文件关联的流
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("./directoryTest/壁纸1.png"));

            while ((res = bis.read(by)) != -1) {
                //Log.i("调试信息", "i" + i + " res: " + res);
                byte[] newByteArr = reviseArr(by, res);
                newByteArr[1] = 1;  // 表示第二个位置上的值为1时表示传输的是文件
                bos.write(newByteArr, 0, res + 2);
                bos.flush();

            }
        } else {
            byte[] sb = str.getBytes(); // 转化为字节数组
            byte[] newByteArr = reviseArr(sb, sb.length);
            newByteArr[1] = 2;      // 表示第二个位置上的值为2时表示传输的是聊天内容
            bos.write(newByteArr);  // 把内容发给服务器
            bos.flush();
            if (str.equalsIgnoreCase("bye")) {
                Log.i("调试信息", "用户下线!");
//                    break;
            }
        }

//        }
    }

    public static void main(String[] args) {
        // 主线程执行写操作，发送消息到服务器

        MultiClient4 mcc = null;
        try {
            mcc = new MultiClient4(new IReceiveListener() {
                @Override
                public void receiveData(String content) {
                    Log.i("调试信息", "接收信息content = " + content);
                }

                @Override
                public void err(Exception e) {

                }
            });
            int cc = 0;
            while (true) {
                mcc.sendMsg(new Msg("测试第一次发送", null));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mcc.stopMultiClient();

        }
    }

    private void stopMultiClient() {
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            mIReceiveListener.err(e);
        }

        try {
            stop();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
            mIReceiveListener.err(e);
        }
    }
}
