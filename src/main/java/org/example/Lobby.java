package org.example;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final String name;
    private final List<ClientHandler> players;

    public Game getGame() {
        return game;
    }

    public boolean started=false;



    private Game game = null;
    public Lobby(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }
    public boolean start(){
        if (players.size()!=4) return false;
        game = new Game(players);
        started=true;


        return true;
    }
    public String getName() {
        return name;
    }


    public List<ClientHandler> getPlayers() {
        return players;
    }

    public void addPlayer(ClientHandler player) {
        players.add(player);
    }

    public boolean removePlayer(ClientHandler player) {
        if (!players.contains(player)) return false;
        players.remove(player);
        return true;
    }

    public int getPlayerCount() {
        return players.size();
    }

    // You can add more lobby-related methods as needed
}
