package com.System_gry_sieciowej;


import java.util.stream.IntStream;

class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int[] board = new int[9];
    private char currentSign = 'x';
    private char[] signMap = { ' ', 'x', 'o' };
    private Player firstPlayer;

    Game() {
        player1 = Server.getPlayersWaiting().get(0);
        player2 = Server.getPlayersWaiting().get(1);
        Server.getPlayersWaiting().remove(0);
        Server.getPlayersWaiting().remove(0);

        // Wylosowanie kto ma pierwszy ruch
        if (Math.round(Math.random()) == 0) {
            firstPlayer = player1;
        } else {
            firstPlayer = player2;
        }
        currentPlayer = firstPlayer;
        this.sendMessage("Stworzono nowa gre");
        this.sendMessage(getBoard());
        currentPlayer.sendMessage("Twój ruch...");
    }

    boolean isCurrentPlayer(Player player) {
        return currentPlayer == player;
    }

    //Czy gracz uczestniczy w danej grze
    boolean hasPlayer(Player player) {
        return player1 == player || player2 == player;
    }

    void move(int placeIndex) {
        if (board[placeIndex] != 0) {
            currentPlayer.sendMessage("Nieprawidlowy ruch");
            return;
        }
        //Przypisanie liczby znakowi
        board[placeIndex] = currentSign == 'x' ? 1 : 2;
        this.sendMessage(this.getBoard());
        Player otherPlayer = currentPlayer == player1 ? player2 : player1;

        int winner = checkWinner();
        if (winner == 1 || winner == 2) {
            currentPlayer.sendMessage("Wygrałeś!!!");
            otherPlayer.sendMessage("Przegrałeś!!!");
            sendMessage("Wygrały " + (currentSign == 'x' ? "Krzyżyki" : "Kółka"));
            Server.getGames().remove(this);
        }else if(winner == 3){
            currentPlayer.sendMessage("Remis!!!");
            otherPlayer.sendMessage("Remis!!!");
            sendMessage("Remis!!!");
            Server.getGames().remove(this);
        }
        else {
            //Zmiana znaku
            currentSign = currentSign == 'x' ? 'o' : 'x';
            //Zmiana ruchu
            currentPlayer = otherPlayer;

            currentPlayer.sendMessage("Twój ruch...");
        }
    }

    private int calcLineWinner(int v1, int v2, int v3) {
        // Wylicza liczbę kontrolą dla linii lub przekątnej
        // Krzyżyk = wartość +1, Kółko = wartość +10
        int lineNumber = 0;
        // Krzyzyki
        if (v1 == 1) lineNumber += 1;
        if (v2 == 1) lineNumber += 1;
        if (v3 == 1) lineNumber += 1;
        // Kółka
        if (v1 == 2) lineNumber += 10;
        if (v2 == 2) lineNumber += 10;
        if (v3 == 2) lineNumber += 10;

        // Wygrana linii: 1+1+1 = 3 - wygrały krzyżyki, 10+10+10 = 30 - wygrały kółka
        if (lineNumber == 3) return 1;
        if (lineNumber == 30) return 2;

        return 0;
    }

    private int checkWinner() {
        // 0 = brak, 1 = krzyżyk, 2 = kółko
        int[][] lines = {
            { 0, 1, 2 },
            { 3, 4, 5 },
            { 6, 7, 8 },
            { 0, 3, 6 },
            { 1, 4, 7 },
            { 2, 5, 8 },
            { 0, 4, 8 },
            { 2, 4, 6 }
        };
        int sum = IntStream.of(board).sum();
        for(int i = 0; i < lines.length; i++) {
            int lineWinner = calcLineWinner(
                board[lines[i][0]],
                board[lines[i][1]],
                board[lines[i][2]]
            );
            if (lineWinner == 1 || lineWinner == 2) {
                return lineWinner;
            }else if(sum == 13 || sum == 14){
                return 3;
            }
        }
        return 0;
    }

    private String getBoard() {
        String result = "";

        for(int i = 0; i < board.length; i++) {
            result += signMap[board[i]];

            // Co trzy printy wyświetl nową linię, inaczej |
            if (i % 3 == 2) {
                result += "\n";
                // Po ostatnim nie wypisuj -----
                if (i < board.length - 1) {
                    result += "-----\n";
                }
            } else {
                result += "|";
            }
        }

        return result;
    }

    private String formatPlayer(Player player) {
        char playerSign = firstPlayer == player ? 'x' : 'o';
        return "(" + player.getId() + ")" + player.getIp() + ":" + player.getPort()+"["+playerSign+"]";
    }

    private void sendMessage(String message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
        Server.sendToSpectators(formatPlayer(player1) + " VS " + formatPlayer(player2));
        Server.sendToSpectators(message);
    }
}
