package cn.mvp.clipper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started. Waiting for client to connect...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected.");

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command ('send', 'sendStr', 'exit'): ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("send")) {
                File file = new File("path/to/large/file");
                FileInputStream fis = new FileInputStream(file);
                long fileSize = file.length();

                out.writeUTF("receiveFile");
                out.writeLong(fileSize);
                out.writeUTF(file.getName());

                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                out.flush();
                fis.close();
                System.out.println("File sent.");

            } else if (command.equalsIgnoreCase("sendStr")) {
                out.writeUTF("receiveStr");
                out.writeUTF("来自服务端消息");
                out.flush();
                System.out.println("Message sent.");

            } else if (command.equalsIgnoreCase("exit")) {
                out.writeUTF("exit");
                out.flush();
                break;
            }
        }

        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
        scanner.close();
        System.out.println("Server closed.");
    }
}