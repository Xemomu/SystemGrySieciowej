package com.System_gry_sieciowej;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Spectator {
    private static final String HOST = "224.0.0.0";
    private static final int PORT = 5051;

    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName(HOST);
            MulticastSocket socket = new MulticastSocket(PORT);
            socket.joinGroup(address);

            while(true) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData());
                System.out.println(received);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}