Program składa się z siedmiu klas: Server, Client, ClientListener, Game, Player, Spectator, SpectatorServer

Server nasłuchuje na połączenie od Client, po nawiązaniu połączenia przypisuje danego Client'a do Player'a, oraz tworzy wątek który obługuje Client'a

Server: 
-tworzy serwer TCP;
-tworzy obiekt klasy SpectatorServer na hoście "224.0.0"(wyjaśnienie w opisie klasy SpectatorServer) i porcie innym  niż Server, oraz rozpoczyna ich wątek;
-tworzy dany socket serwera;
-tworzy nowy obiekt gracza, do którego przypisuje klienta, z którym łączy się z pomocą metody "listen()", która czeka na jego połączenie i je akceptuje;
-dodaje gracza do wcześniej stworzonej listy graczy;
-tworzy i rozpoczyna nowy wątek ClientListener przyjmujący znalezionego gracza;
-tworzy metodę "findGameByPlayer()" pozwalającą na znalezienie gry, do której należy dany gracz;
-tworzy metodę "sendToSpectators()" wysyłającą do widzów dane graczy;
-tworzy obiekt klasy SpectatorServer, oraz rozpoczyna nowy wątek tej klasy;

Client:
-tworzy klienta poprzednio stworzonego serwera TCP;
-tworzy nowy socket o takim samy porcie jak Server i hoscie "localhost";
-tworzy i rozpoczyna nowe wątki do wysyłania i odbierania danych z pomocą PrintWriter i BufferedReader;
-tworzy metodę "sendMessage" i  używa jej w celu wysyłania danych na dany socket(serwer);

ClientListener:
-klasa implementuje interfejs "Runnable", pozwalając na implementację klasy w wielu wątkach na serwerze;
-konstruktor przyjmuje gracza jako argument;
-metoda "run()" rozpoczynająca się przy tworzeniu wątku z użyciem ClientListener;
-"run()" wypisuje dla kakiego klienta został uruchomiony wątek; zawiera pętlę "while(active)" zmieniającą się na false w momencie wpisania przez klienta "LOGOUT", powoduje to zatrzymanie możliwości interakcji klienta z serwerem;
 tworzy pole typu String przechowujące dane otrzymane od klienta znalezionego w klasie Server;
 tworzy obiekt klasy Game znajdując grę poprzez Player'a otrzymanego w konstruktorze klasy;
 sprawdza, czy gracz jest w grze i "dobiera" zakres poleceń, których może użyc gracz;
 jeżeli jest w grze, może on tylko użyć cyfr (1-9) w celu wybrania pola, a jeżeli jest poza grą może on sprawdzić    listę graczy, wylogować się, oraz rozpocząć grę/dodać się do listy oczekujących na grę;
 sprawdza także, czy jest kolej danego gracza
 z pomocą switch'a zostały zaimplementowane trzy polecenia, których może użyć klient poza grą
 ~LIST- wypisuje z pomocą ArrayList i metody "sendMessage" z klasy "Player" wszystkich graczy, oraz ich ID, IP i Port 
 ~LOGOUT- usuwa klienta z listy graczy, oraz zatrzymuje działanie pętli "while(active)"
 ~PLAY- dodaje klienta do listy graczy oczekujących, jeżeli ta lista jest większa od 1 tworzy on nowy obiekt klasy    Game i dodaje ją do listy gier

Player:
-w konstruktorze przyjmuje socket klienta, który otrzymuje od klasy serwer, oraz ID które jest zwiększane o jeden  przy każdym stworzeniu obiektu klasy Player;
-wysyła dane do klienta z pomocą PrintWriter, oraz metody "sendMessage()" używającej ".println()";
-odbiera dane od klienta z pomocą BufferedReader, oraz metody "reciever()" używającej ".readLine()";
-metoda "getIp()" zwracająca w Stringu Ip klienta;
-metoda "getPort()" zwracająca w Stringu port klienta;

Game:
-inicjuje dwa pola klasy Player, przypisuje do każdego z nich po jednym graczu z listy oczekujących, oraz usuwa tych  dwóch graczy z tej listy;
-wypisywanie powiadomień typu "Twój ruch"itp. do gracza jest obsłużone przez metodę "sendMessage()" klasy Player
-losuje kto ma pierwszy ruch za pomocą "Math.random()";
-przypisuje wylosowanego gracza jako aktualnego;
-wyświetla planszę;
-metoda "boolean isCurrentPlayer()" sprawdzająca, czy dany gracz jest tym mającym ruch;
-metoda "boolean hasPlayer()" sprawdzająca, czy gracz należy do danej gry;
-metoda "move()"
 sprawdza, czy pole którego chce użyć gracz jest puste
 przypisuje wartość "1" jeżeli obecny znak == "x"(tak jest domyślnie przypisany jako znak pierwszy), jeżeli jest to   "o" przypisuje mu wartosć "2"
 wyświetla planszę
 z pomocą metody "checkWinner()" ogłasza wygranego, oraz usuwa grę z listy lub kontynuuje grę zmieniając znak i  obecnego gracza
 -metoda "calcLineWinner()" wylicza liczbę kontrolną dla lini lub przekątnej, gdzie 'x' = wartość+1, 
 a 'o'=wartość+10
 jeżeli w danej lini wyliczy sume 30 lub 3 zwraca odpowiednio wygranego, a jeżeli nic zwraca jako remis
-metoda "checkWinner()" tworzy dwuwymiarową tablicę możliwych lini(poziomych, pionowych, ukośnych)
 sprawdza czy na planszy nie znajduje się wygrana lini z pomocą metody "calcLineWinner()" i zwraca kto wygrał, jeżeli suma tablicy ==13 || ==14 zwraca remis, 13,14 ponieważ jest to suma całej planszy x,o w momencie, gdy zaczyna x lub o; 
-metoda "getBoard()" służy do wyświetlania planszy, jest to proste wyświetlanie z pomocą String;
-metoda "formatPlayer()", która w przejrzysty sposób wypisuje dane gracza;
-metoda "sendMessage()" wysyłająca do obu graczy planszę za pomocą metody "sendMessage()" klasy Player, oraz do widzów  dane kto gra przeciwko komu z planszą za pomocą "sendToSpectators()" klasy Server-> SpectatorServer(szczegóły w  opisie klasy SpectatorServer);

SpectatorServer:
-implementuje Runnable;
-tworzy serwer UDP;
-przypisuje InetAdresowi host "224.0.0"(ten adres, ponieważ został użyty MulticastSocket i zgodnie z RFC 3171 jest to  jeden z adresów wyznaczonych do obsługi multicast; adres ten jest adresem lokalnym), oraz port inny niż naszego  serwera TCP;
-BufferedReader(Piped Reader) odczytuje dane przysłane przez PipedWriter;
-tworzy nowy serversocket, w którym używamy MulticastSocket(został on użyty ponieważ jest on wygodny w momencie, gdy chcemy jedynie rozesłać dane do wielu klientów), oraz dodajemy ten socket do grupy multicast;
-metoda "sendMessage()" wysyła dane za pomocą PipedWriter do klienta tego serwera(Spectator), PipedWriter ponieważ  jest on w stanie w prosty sposób przesłać dane z jednego wątka do drugiego, u nas z Game do SpectatorServer;
-metoda "run()" w pętli "while(true)" zczytuje dane odczytane przez BufferedReader z PipedReader;

Spectator:
-jest to klient SpectatorServer, który go nasłuchuje;
-przypisujemy mu takiego samego hosta i port co SpectatorServer;
-tworzymy i przypisujemy mu nowy multicast socket, oraz dodajemy ten socket do grupy multicast;
-w pętli "while(true)" przypisujemy do tablicy typu byte nadchodzące dane;
-przypisujemy otrzymane dane do String oraz je wyświetlamy 



Wszystko powinno działać zgodnie z podaną dokumentacją zadania
Program należy uruchomić jako projekt w inteliJ IDEA
W pierwszej kolejności należy uruchomić klasę Server, następnie można uruchomić klasę Client lub Spectator(Widz)
Klasy Client i Spectator możemy uruchomić > raz

Jako Client możemy używać komend w :
-PLAY - pokazuje serwerowi naszą chęć zagrania
-LIST - pokazuje listę klientów
-LOGOUT - odłącza nas od serwera
 W czasie gry możemy jedynie używać cyfr 1-9 w celu wybrania pola

Jako Spectator możemy jedynie oglądać rozgrywki

