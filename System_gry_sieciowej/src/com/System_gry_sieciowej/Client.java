package com.System_gry_sieciowej;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5050;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(InetAddress.getByName(HOST), PORT);
            System.out.println("Polaczono z serwerem");

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Nowy watek do wysylania, aby nie blokować programu
            new Thread(() -> {
                while(true){
                    String line = new Scanner(System.in).nextLine();
                    sendMessage(line);
                }
            }).start();

            //Nowy watek do odbierania, aby nie blokować programu
            new Thread(() -> {
                while(true){
                    try {
                        String str = in.readLine();
                        System.out.println(str);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String msg) {
        out.println(msg);
    }
}
