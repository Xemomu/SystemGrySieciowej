package com.System_gry_sieciowej;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static final int PORT = 5050;
    private static ServerSocket serverSocket;
    private static SpectatorServer spectatorServer;
    private static ArrayList<Player> players = new ArrayList<>();
    private static ArrayList<Player> players_waiting = new ArrayList<>();
    private static ArrayList<Game> games = new ArrayList<>();
    private static int id = 0;

    public static void main(String[] args) {
        spectatorServer = new SpectatorServer("224.0.0.0", PORT + 1);
        new Thread(spectatorServer).start();

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            Player player = new Player(listen(), id++);
            players.add(player);
            Thread thread = new Thread(new ClientListener(player));
            thread.start();
            System.out.println("Polaczono z nowym klientem");
            for(int i = 0; i<players.size(); i++){
                System.out.println(players.get(i));
            }
        }
    }

    private static Socket listen(){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static ArrayList<Player> getPlayers() {
        return players;
    }

    static ArrayList<Player> getPlayersWaiting() {
        return players_waiting;
    }

    static ArrayList<Game> getGames() {
        return games;
    }

    //Znalezienie gry, do której należy gracz

    static Game findGameByPlayer(Player player) {
        for(int i = 0; i < games.size(); i++) {
            if (games.get(i).hasPlayer(player)) {
                return games.get(i);
            }
        }
        return null;
    }

    static void sendToSpectators(String message) {
        spectatorServer.sendMessage(message);
    }
}
