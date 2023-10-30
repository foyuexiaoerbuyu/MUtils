package cn.mvp.mlibs.socket;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import cn.mvp.mlibs.utils.FileUtils;
import cn.mvp.mlibs.utils.GsonUtil;


/**
 * https://www.runoob.com/java/net-serversocket-socket.html
 */

public class SocketUtils {

    private static final int BUFFER_SIZE = 1024 * 1024; // 1 MB
    private final IServiceNotifyMsg iServiceNotifyMsg;
    private ServerSocket serverSocket;
    private Socket mClientSocket;
    private Socket mServiceSocket;

    public SocketUtils(IServiceNotifyMsg iServiceNotifyMsg) {
        this.iServiceNotifyMsg = iServiceNotifyMsg;
    }

    /**
     * 启动服务端
     *
     * @param port         服务器端口
     * @param iReceiverMsg 接收客户端消息回调
     */
    public void startService(int port, String receiverPath, IReceiverMsg iReceiverMsg) {
        new Thread(() -> {
            try {
                FileUtils.makeDirs(receiverPath);
                serverSocket = new ServerSocket(port);
                System.out.printf("启动服务 %d\n", serverSocket.getLocalPort());

                while (true) {
                    mServiceSocket = serverSocket.accept();
                    BufferedReader serviceReader = new BufferedReader(new InputStreamReader(mServiceSocket.getInputStream()));
                    receiverMsg(receiverPath, iReceiverMsg, serviceReader);
                }

            } catch (IOException e) {
                iServiceNotifyMsg.errMsg(e, "启动服务器失败");
                e.printStackTrace();
            } finally {
                try {
                    if (mServiceSocket != null) {
                        mServiceSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 客户端连接服务器
     *
     * @param iReceiverMsg 接收服务端消息
     */
    public void connService(List<String> ips, String receiverPath, IReceiverMsg iReceiverMsg) {
        new Thread(() -> {
            try {
                for (String ip : ips) {
                    if (ip.contains(":")) {
                        String[] split = ip.split(":");
                        String host = split[0];
                        int port = Integer.parseInt(split[1]);
                        try {
                            mClientSocket = new Socket(host, port);
                            break;
                        } catch (IOException e) {
                        }
                    }
                }
                if (mClientSocket == null) {
                    iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器异常.");
                    return;
                }
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器成功");
                sendMsgToService("客户端连接服务器成功");
                receiverMsg(receiverPath, iReceiverMsg, clientReader);
            } catch (IOException e) {
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器异常");
                iServiceNotifyMsg.errMsg(e, "连接服务器异常");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 客户端连接服务器
     *
     * @param host         服务器地址
     * @param port         端口
     * @param iReceiverMsg 接收服务端消息
     */
    public void connService(String host, int port, String receiverPath, IReceiverMsg iReceiverMsg) {
        new Thread(() -> {
            try {
                mClientSocket = new Socket(host, port);

                BufferedReader clientReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器成功");
                sendMsgToService("客户端连接服务器成功");
                receiverMsg(receiverPath, iReceiverMsg, clientReader);
            } catch (IOException e) {
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器异常");
                iServiceNotifyMsg.errMsg(e, "连接服务器异常");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 发送原始字符串数据给服务端(标记过期只是标识一下)
     *
     * @param str 客户端发消息给服务端(原始数据)
     */
    @Deprecated
    private void sendMsgToServicePrimitive(String str) {
        try {
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
            clientWriter.write(str);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            iServiceNotifyMsg.errMsg(e, "客户端发送消息异常");
            e.printStackTrace();
        }
    }

    /**
     * 发送原始字符串数据给客户端(标记过期只是标识一下)
     *
     * @param str 服务端发消息给客户端
     */
    @Deprecated
    private void sendMsgToClientPrimitive(String str) {
        try {
            BufferedWriter serviceWriter = new BufferedWriter(new OutputStreamWriter(mServiceSocket.getOutputStream()));
            serviceWriter.write(str);
            serviceWriter.newLine();
            serviceWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            iServiceNotifyMsg.errMsg(e, "服务端发送消息异常");
        }
    }

    /**
     * @param clientCall 消息回调
     * @param reader     输入流
     */
    private void receiverMsg(String receiverPath, IReceiverMsg clientCall, BufferedReader reader) {
        String msg;
        try {
            while ((msg = reader.readLine()) != null) {
                System.out.println("msg = " + msg);
                ChatMsg chatMsg = GsonUtil.fromJson(msg, ChatMsg.class);
                if (chatMsg.getMsgType() == ChatMsgType.MSG_TYPE_FILE) {
//                    XLog.showLogArgs(receiverPath, chatMsg.getFileName(), chatMsg.getMd5());
                    String filePath = receiverPath + chatMsg.getFileName();
                    if (FileUtils.isFileExist(filePath) && FileUtils.getFileMD5(filePath).equals(chatMsg.getMd5())) {
                        clientCall.receiverMsg("接收文件完毕");
                        continue;
                    }
                    FileUtils.writeFile(filePath, chatMsg.getFileData(), true);
                } else if (chatMsg.getMsgType() == ChatMsgType.MSG_TYPE_MSG) {
                    clientCall.receiverMsg(chatMsg.getMsgContent().trim());
                }
                if ("Bye".equals(chatMsg.getMsgContent())) {
                    clientCall.receiverMsg("已下线");
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

    //-----------------------------------------------------------------------------------------
    public void sendMsgToClient(String msg) {
        sendMsgToClientPrimitive(new ChatMsg(msg).toJson());
    }

    public interface IReceiverMsg {
        /*0:普通日志*/
        static final int MSG_TYPE_COMM_LOG = 0;

        void receiverMsg(String receiveMsg);

        default void progress(int progress) {

        }

        default void log(int type, String msg) {

        }
    }

    @FunctionalInterface
    public interface IServiceNotifyMsg {
        void errMsg(Exception e, String errMsg);
    }

    public void sendMsgToService(String msg) {
        sendMsgToServicePrimitive(new ChatMsg(msg).toJson());
    }

    public void sendFileToService(String filePath) {
        File file = new File(filePath);
        String md5 = FileUtils.getFileMD5(file);
        System.out.println("start send file----" + file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                System.out.println("bytesRead = " + bytesRead);
                byte[] dataToSend = Arrays.copyOf(buffer, bytesRead);
                ChatMsg fileMsg = new ChatMsg(file.getName(), md5, file.length(), dataToSend);
                sendMsgToServicePrimitive(fileMsg.toJson());
            }

            System.out.println("File send: " + filePath);
        } catch (IOException e) {
            sendMsgToClient("接收文件异常: " + e.getMessage());
            iServiceNotifyMsg.errMsg(e, "接收文件异常: " + e.getMessage());
            System.err.println("Error sending file: " + e.getMessage());
        }
    }

    public void sendFileToClient(String filePath) {
        File file = new File(filePath);
        String md5 = FileUtils.getFileMD5(file);
        System.out.println("start send file" + file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] dataToSend = Arrays.copyOf(buffer, bytesRead);
                ChatMsg fileMsg = new ChatMsg(file.getName(), md5, file.length(), dataToSend);
                sendMsgToClientPrimitive(fileMsg.toJson());
            }

            System.out.println("File send: " + filePath);
        } catch (IOException e) {
            sendMsgToService("接收文件异常: " + e.getMessage());
            iServiceNotifyMsg.errMsg(e, "接收文件异常: " + e.getMessage());
            System.err.println("Error sending file: " + e.getMessage());
        }
    }
}