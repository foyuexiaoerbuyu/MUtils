package cn.mvp.chat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private static final int BROADCAST_PORT = 8888;


    public static void start() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    main(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setBroadcast(true);

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        String message = "Hello from client!";
        sendData = message.getBytes();

// Send the broadcast packet
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), BROADCAST_PORT);
        clientSocket.send(sendPacket);

// Receive the response from the server
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String serverResponse = new String(receivePacket.getData());

        System.out.println("RESPONSE FROM SERVER: " + serverResponse);

        clientSocket.close();
    }
}