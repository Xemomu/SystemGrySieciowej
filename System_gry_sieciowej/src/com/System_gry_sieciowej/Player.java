package com.System_gry_sieciowej;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Player {
    private Socket client;
    private int id;
    private PrintWriter out;
    private BufferedReader in;

    Player(Socket client, int id) {
        this.client = client;
        this.id = id;

        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String msg) {
        out.println(msg);
    }

    String reciever(){
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    int getId() {
        return id;
    }

    String getIp() {
        return client.getInetAddress().toString();
    }

    int getPort() {
        return client.getLocalPort();
    }

}