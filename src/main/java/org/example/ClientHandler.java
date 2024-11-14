package org.example;

import org.example.Server;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    Lobby lobby = null;

    public void setUsername(String username) {
        this.username = username;
    }

    public String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.username=Server.generateNickname(10);
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                handleCommands(inputLine);
            }
        } catch (IOException | JSONException e) {
            if(lobby!=null){
                if (lobby.started){
                    lobby.getGame().endGame(1);
                }
            }
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                Server.clients.remove(this);
                System.out.println("Client disconnected: " + socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void handleCommands(String command) throws JSONException {
        try{
        JSONObject jsonCommand = new JSONObject(command);
        if (Objects.equals(jsonCommand.getString("type"), "login")){
            if(Auth.checkLogin(jsonCommand.getString("name"),jsonCommand.getString("password"))){
                for (ClientHandler c:Server.clients){
                    if (Objects.equals(jsonCommand.getString("name"), c.username)){
                        sendMessage("already logged in");
                        return;
                    }
                }
                username = jsonCommand.getString("name");
                sendMessage("login success");
            }
            else sendMessage("login failed");
        }
        else if (Objects.equals(jsonCommand.getString("type"), "register")){
            boolean res = Auth.registerUser(jsonCommand.getString("name"),Auth.hashPassword(jsonCommand.getString("password")));
                if (res) sendMessage("register successfully");
                else sendMessage("register failed");
        }
        else if (Objects.equals(jsonCommand.getString("type"), "create_lobby")) {
            lobby = Server.createLobby(jsonCommand.getString("name"),this);
        }
        else if (Objects.equals(jsonCommand.getString("type"), "list_lobbies")) {
            Server.listLobbies(this);
        }
        else if (Objects.equals(jsonCommand.getString("type"), "start")) {
            Server.startLobby(jsonCommand.getString("name"));
        }
        else if (Objects.equals(jsonCommand.getString("type"), "join_lobby")) {
            lobby = Server.joinLobby(jsonCommand.getString("name"), this);
        }
        else if (Objects.equals(jsonCommand.getString("type"), "leave_lobby")) {
            Server.leaveLobby(jsonCommand.getString("name"), this);
            lobby=null;
        }
        else if (Objects.equals(jsonCommand.getString("type"), "invite")) {
            Server.invitePlayer(this,jsonCommand.getString("name"));
        }
        else if (Objects.equals(jsonCommand.getString("type"), "play")) {
            if (lobby!=null){
                if(lobby.started){
                    if(!lobby.getGame().playCard(jsonCommand.getString("card"),this)){
                        sendMessage("cannot play this card");
                    }
                }
                else sendMessage("lobby not started");
            }
            else {
                sendMessage("you are not in lobby");
            }

        }
        else {
            sendMessage("unknown command");
        }}
        catch (JSONException e){
            sendMessage("unknown command");
        }
    }
    void sendMessage(String message) {
        out.println(message);
    }
}