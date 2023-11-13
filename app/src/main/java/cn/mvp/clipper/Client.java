package cn.mvp.clipper;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("Connected to server.");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

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
                out.writeUTF("来自客户端消息");
                out.flush();
                System.out.println("Message sent.");

            } else if (command.equalsIgnoreCase("exit")) {
                out.writeUTF("exit");
                out.flush();
                break;
            }

            String serverCommand = in.readUTF();

            if (serverCommand.equalsIgnoreCase("receiveFile")) {
                long fileSize = in.readLong();
                String fileName = in.readUTF();

                FileOutputStream fos = new FileOutputStream("received/" + fileName);
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;

                while (totalBytesRead < fileSize && (bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                fos.close();
                System.out.println("File received: " + fileName);

            } else if (serverCommand.equalsIgnoreCase("receiveStr")) {
                String message = in.readUTF();
                System.out.println("Message received: " + message);

            } else if (serverCommand.equalsIgnoreCase("exit")) {
                break;
            }
        }

        in.close();
        out.close();
        socket.close();
        scanner.close();
        System.out.println("Client closed.");
    }
}