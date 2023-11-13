package cn.mvp.clipper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileTransfer {

    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 8192;
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 'server' or 'client': ");
        String mode = scanner.nextLine();

        if ("server".equalsIgnoreCase(mode)) {
            runServer();
        } else if ("client".equalsIgnoreCase(mode)) {
            runClient();
        } else {
            System.out.println("Invalid mode.");
        }
    }

    private static void runServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClient(clientSocket));
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream();
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("Enter 'send' to send a file, 'sendStr' to send a message or 'exit': ");
                String command = scanner.nextLine();

                if ("send".equalsIgnoreCase(command)) {
                    System.out.println("Enter file path: ");
                    String filePath = scanner.nextLine();
                    sendFile(filePath, outputStream);
                } else if ("sendStr".equalsIgnoreCase(command)) {
                    String message = "来自服务端消息";
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                } else if ("exit".equalsIgnoreCase(command)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runClient() throws IOException {
        try (Socket socket = new Socket("localhost", PORT);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server");

            while (true) {
                System.out.println("Enter 'send' to send a file, 'sendStr' to send a message or 'exit': ");
                String command = scanner.nextLine();

                if ("send".equalsIgnoreCase(command)) {
                    System.out.println("Enter file path: ");
                    String filePath = scanner.nextLine();
                    sendFile(filePath, outputStream);
                } else if ("sendStr".equalsIgnoreCase(command)) {
                    String message = "来自客户端消息";
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                } else if ("exit".equalsIgnoreCase(command)) {
                    break;
                }
            }
        }
    }

    private static void sendFile(String filePath, OutputStream outputStream) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        long fileSize = file.length();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesSent = 0;
            while ((bytesRead = bis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesSent += bytesRead;
                System.out.printf("Progress: %.2f%%\n", totalBytesSent * 100.0 / fileSize);
            }
            outputStream.flush();
        }
        System.out.println("File sent.");
    }
}