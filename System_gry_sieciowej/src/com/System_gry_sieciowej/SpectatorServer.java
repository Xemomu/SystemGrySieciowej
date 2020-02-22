package com.System_gry_sieciowej;

import java.io.*;
import java.net.*;

public class SpectatorServer implements Runnable {
    private MulticastSocket serverSocket;
    private PipedWriter pw;
    private BufferedReader br;
    private InetAddress address;
    private int serverPort;

    SpectatorServer(String host, int serverPort) {
        try {
            this.address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.serverPort = serverPort;
        this.pw = new PipedWriter();

        try {
            this.br = new BufferedReader(new PipedReader(pw));
            this.serverSocket = new MulticastSocket(serverPort);
            this.serverSocket.joinGroup(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String message) {
        try {
            pw.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                String line = br.readLine();
                System.out.println(line);
                DatagramPacket p = new DatagramPacket(
                    line.getBytes(),
                    line.getBytes().length,
                    address,
                    serverPort
                );
                serverSocket.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
