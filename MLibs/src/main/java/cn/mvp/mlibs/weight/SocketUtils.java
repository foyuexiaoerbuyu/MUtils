package cn.mvp.mlibs.weight;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    private Socket mClientSocket;

    private BufferedWriter clientWriter;
    private BufferedReader clientReader;
    private BufferedWriter serviceWriter;
    private BufferedReader serviceReader;
    private Socket mServiceSocket;

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
                    mServiceSocket = serverSocket.accept();
                    serviceWriter = new BufferedWriter(new OutputStreamWriter(mServiceSocket.getOutputStream()));
                    serviceReader = new BufferedReader(new InputStreamReader(mServiceSocket.getInputStream()));

                    receiverMsg(serviceCall, serviceReader);

                    mServiceSocket.close();
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
            mClientSocket = new Socket(host, port);
            clientWriter = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
            clientReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));

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
            mClientSocket.close();
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
//-----------------------------------------------------------------------------------------

    /**
     * @param filePath 文件路径
     */
    public void serviceSendFile(String filePath) {
        if (serviceWriter == null) {
            iServiceNotifyMsg.errMsg(new RuntimeException("没起服务器"), "没起服务器");
            System.out.println("没起服务器");
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                iServiceNotifyMsg.errMsg(new RuntimeException("文件不存在"), "文件不存在");
                System.out.println("文件不存在");
                return;
            }

            // 发送文件名
            serviceWriter.write(file.getName());
            serviceWriter.newLine();
            serviceWriter.flush();

            byte[] buffer = new byte[1024];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream bos = new BufferedOutputStream(mServiceSocket.getOutputStream());

            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            iServiceNotifyMsg.errMsg(e, "服务端发送文件异常");
        }
    }

    /**
     * @param savePath 文件保存路径
     */
    public void serviceReceiveFile(String savePath) {
        try {
            String fileName = serviceReader.readLine();
            if (fileName == null) {
                iServiceNotifyMsg.errMsg(new RuntimeException("接收文件名失败"), "接收文件名失败");
                System.out.println("接收文件名失败");
                return;
            }

            File file = new File(savePath, fileName);

            byte[] buffer = new byte[1024];
            BufferedInputStream bis = new BufferedInputStream(mServiceSocket.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            bis.close();
            bos.close();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "服务端接收文件异常");
            e.printStackTrace();
        }
    }

    /**
     * @param filePath 文件路径
     */
    public void clientSendFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                iServiceNotifyMsg.errMsg(new RuntimeException("文件不存在"), "文件不存在");
                System.out.println("文件不存在");
                return;
            }

            // 发送文件名
            clientWriter.write(file.getName());
            clientWriter.newLine();
            clientWriter.flush();

            byte[] buffer = new byte[1024];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream bos = new BufferedOutputStream(mClientSocket.getOutputStream());

            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            bis.close();
            bos.close();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "客户端发送文件异常");
            e.printStackTrace();
        }
    }

    /**
     * @param savePath 文件保存路径
     */
    public void clientReceiveFile(String savePath) {
        try {
            String fileName = clientReader.readLine();
            if (fileName == null) {
                iServiceNotifyMsg.errMsg(new RuntimeException("接收文件名失败"), "接收文件名失败");
                System.out.println("接收文件名失败");
                return;
            }

            File file = new File(savePath, fileName);

            byte[] buffer = new byte[1024];
            BufferedInputStream bis = new BufferedInputStream(mClientSocket.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            bis.close();
            bos.close();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "客户端接收文件异常");
            e.printStackTrace();
        }
    }

}
