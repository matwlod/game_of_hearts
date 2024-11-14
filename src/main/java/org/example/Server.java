package org.example;
import org.example.ClientHandler;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 23232;
    static final List<ClientHandler> clients = new ArrayList<>();
    static final Map<String, Lobby> lobbies = new HashMap<>();

    static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    static void startServer(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Thread(Server::handleConsoleInput).start();
        startServer();

    }
    static Lobby createLobby(String lobbyName,ClientHandler client) {
        if (!lobbies.containsKey(lobbyName)) {
            Lobby lobby = new Lobby(lobbyName);
            lobbies.put(lobbyName, lobby);
            client.sendMessage("Lobby created: " + lobbyName);
            return joinLobby(lobbyName,client);
        }
        else {
            client.sendMessage("lobby with that name already exists");
            return null;
        }

    }

    static void listLobbies(ClientHandler client) {
        StringBuilder lobbyList = new StringBuilder("Available lobbies:\n");
        for (Lobby lobby : lobbies.values()) {
            lobbyList.append(lobby.getName()).append(" (").append(lobby.getPlayerCount()).append("/4)\n");
            for (ClientHandler c:lobby.getPlayers()){
                lobbyList.append(c.username).append("\n");
            }
        }
        client.sendMessage(lobbyList.toString());
    }

    static Lobby joinLobby(String lobbyName, ClientHandler client) {
        if (lobbies.containsKey(lobbyName)) {
            Lobby lobby = lobbies.get(lobbyName);
            if (lobby.getPlayers().size()==4){
                client.sendMessage("lobby full");
                return null;
            }
            lobby.addPlayer(client);
            client.sendMessage("joined lobby: " + lobbyName);
            return lobby;
        } else {

            client.sendMessage("Lobby '" + lobbyName + "' not found.");
            return null;
        }
    }
    static void invitePlayer(ClientHandler client,String username) {
        if (client.lobby==null)
        {
            client.sendMessage("you are not in lobby");
            return;
        }
        for (ClientHandler c:clients){
            if(Objects.equals(c.username, username)){
                c.sendMessage(client.username+" invited you to lobby "+client.lobby.getName());
            }
        }
    }

    static void leaveLobby(String lobbyName, ClientHandler client) {
        if (lobbies.containsKey(lobbyName)) {
            Lobby lobby = lobbies.get(lobbyName);
            if(lobby.removePlayer(client)){
                client.sendMessage("you left lobby");
            }
            else {
                client.sendMessage("you are not in this lobby");
            }

        } else {
            client.sendMessage("Lobby '" + lobbyName + "' not found.");
        }
    }
    static void startLobby(String lobbyName) {
        if (lobbies.containsKey(lobbyName)) {
            Lobby lobby = lobbies.get(lobbyName);
            if(lobby.start()){
            for (ClientHandler player : lobby.getPlayers()){
                player.sendMessage("started lobby");
            }}
            else {
                for (ClientHandler player : lobby.getPlayers()){
                    player.sendMessage("failed to start lobby");
                }
            }

        }

    }
    static void handleConsoleInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input.startsWith("skip")){
                try {
                    String lobbyName = input.split(" ")[1];
                    if (lobbies.containsKey(lobbyName)) {
                        Lobby lobby = lobbies.get(lobbyName);
                        if(lobby.started){
                            lobby.getGame().skipRound();
                        }
                    }
                }
                catch (IndexOutOfBoundsException e){
                    continue;
                }


        }}
    }
    public static String generateNickname(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
}
