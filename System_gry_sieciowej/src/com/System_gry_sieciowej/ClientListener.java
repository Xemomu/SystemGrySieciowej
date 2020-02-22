package com.System_gry_sieciowej;


import java.util.ArrayList;

public class ClientListener implements Runnable{
    private Player client;

    ClientListener(Player client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Uruchomiono nowy watek do obslugi klienta " + client.getId() + client.getIp());
        boolean active = true;
        while(active){
            String rec = client.reciever();
            Game game = Server.findGameByPlayer(client);

            if (game != null) {
                // Komendy gry (wskazanie wybranego pola)
                if (game.isCurrentPlayer(client)) {
                    try {
                        //Zamiana String na int
                        int place = Integer.parseInt(rec);
                        if (place >= 1 && place <= 9) {
                            game.move(place - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    client.sendMessage("Nie twoja kolej");
                }
                continue;
            }

            switch (rec){
                case "LIST" :
                    //obsługa LIST
                    String result = "Gracze:\n";
                    ArrayList<Player> players = Server.getPlayers();
                    for(int i = 0; i < players.size(); i++) {
                        result += "ID: " + players.get(i).getId() +
                                ", IP: " + players.get(i).getIp() +
                                ", Port: " + players.get(i).getPort() + "\n";
                    }
                    client.sendMessage(result);
                    break;
                case "LOGOUT" :
                    //obsługa LOGOUT
                    client.sendMessage("logout");
                    Server.getPlayers().remove(client);
                    active = false;
                    break;
                case "PLAY" :
                    //obsługa PLAY
                    Server.getPlayersWaiting().add(client);
                    client.sendMessage("Szukanie przeciwnika...");
                    if(Server.getPlayersWaiting().size()>1) {
                        System.out.println("GRA!!!");
                        Server.getGames().add(new Game());
                    }
                    break;
                default:
                    client.sendMessage("Zla komenda");
            }
        }
    }
}