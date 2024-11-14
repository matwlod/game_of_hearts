package org.example;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.List;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 23232;

    private static GUI gui = null;
    private static Socket socket;
    private static  BufferedReader in;
    private static boolean logged = false;

    private static PrintWriter out;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Welcome to our haircuts.com !!!.\nPlease login or register using:\nregister {username} {password}\nlogin {username} {password}\nor continue without authentication - your name will be randomly generated\n");

            startReceivingThread(out,in);
            startSendingThread(out, userInput);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startReceivingThread(PrintWriter out,BufferedReader in) {
        Thread receiveThread = new Thread(() -> {
            String serverResponse;
            try {
                while ((serverResponse = in.readLine()) != null) {
                    handleCommand(out,serverResponse);
                }
            } catch (IOException e) {
                System.out.println("disconnected from server");
                System.exit(0);
            }
        });
        receiveThread.start();
    }
    public static void handleCommand(PrintWriter out,String response){
        if (response.isEmpty()) return;
       if (response.charAt(0)=='{'){
           if (gui==null) gui=new GUI(out);
           gui.updateGUI(response);
           //System.out.println(response);
        }
        else {
            if(response.startsWith("GAME ENDED")){
                gui.closeWindow();
            }
            System.out.println(response);
        }
    }


    public static void startSendingThread(PrintWriter out, BufferedReader userInput) throws IOException {
        String userInputLine;
        while ((userInputLine = userInput.readLine()) != null) {
            if ("exit".equalsIgnoreCase(userInputLine)) {
                break;
            }
            processUserInput(out, userInputLine);
        }
    }

    public static void processUserInput(PrintWriter out, String userInputLine) {
        if (userInputLine.startsWith("login")) {
            handleLogin(out, userInputLine);
        } else if (userInputLine.startsWith("register")) {
            handleRegister(out, userInputLine);
        }
        else if (userInputLine.startsWith("create")) {
            handleCreateLobby(out, userInputLine);
        }
        else if (userInputLine.startsWith("list")) {
            handleListLobbies(out);
        }
        else if (userInputLine.startsWith("leave")) {
            handleLeaveLobby(out, userInputLine);
        }
        else if (userInputLine.startsWith("join")) {
            handleJoinLobby(out, userInputLine);
        }else if (userInputLine.startsWith("start")) {
            startGame(out, userInputLine);
        }else if (userInputLine.startsWith("play")) {
            playCard(out,userInputLine);
        }else if (userInputLine.startsWith("invite")) {
            invite(out,userInputLine);
        }else {
            out.println(userInputLine);
        }
    }

    public static void handleLogin(PrintWriter out, String userInputLine) {
        try {
            userLogin(out,userInputLine.split(" ")[1], userInputLine.split(" ")[2]);
        } catch (IndexOutOfBoundsException | JSONException e) {
            System.out.println("wrong command");
        }

    }

    private static void handleRegister(PrintWriter out, String userInputLine) {

        try {
             userRegister(out,userInputLine.split(" ")[1], userInputLine.split(" ")[2]);
        } catch (IndexOutOfBoundsException | JSONException e) {
            System.out.println("wrong command");
        }

    }

    private static void userLogin(PrintWriter out,String username, String password) throws JSONException {

        JSONObject loginRequest = new JSONObject();
        loginRequest.put("type", "login");
        loginRequest.put("name", username);
        loginRequest.put("password", password);
        out.println(loginRequest.toString());

    }
    private static void invite(PrintWriter out,String username) throws JSONException {

        JSONObject loginRequest = new JSONObject();
        loginRequest.put("type", "invite");
        loginRequest.put("name", username.split(" ",2)[1]);
        out.println(loginRequest.toString());

    }
    private static void startGame(PrintWriter out, String userInputLine) throws JSONException {

        JSONObject start = new JSONObject();
        start.put("type", "start");
        start.put("name", userInputLine.split(" ")[1]);

        out.println(start.toString());

    }

    private static void userRegister(PrintWriter out,String username, String password) throws JSONException {
        JSONObject loginRequest = new JSONObject();
        loginRequest.put("type", "register");
        loginRequest.put("name", username);
        loginRequest.put("password", password);
        out.println(loginRequest.toString());

    }
    private static void handleCreateLobby(PrintWriter out, String userInputLine) {
        try {
            createLobby(out, userInputLine.split(" ",2)[1]);
        } catch (IndexOutOfBoundsException | JSONException e) {
            System.out.println("wrong command");
        }
    }

    private static void handleListLobbies(PrintWriter out) {
        listLobbies(out);
    }

    private static void handleJoinLobby(PrintWriter out, String userInputLine) {
        try {
            joinLobby(out, userInputLine.split(" ",2)[1]);
        } catch (IndexOutOfBoundsException | JSONException e) {
            System.out.println("wrong command");
        }
    }

    private static void handleLeaveLobby(PrintWriter out, String userInputLine) {
        try {
            leaveLobby(out, userInputLine.split(" ",2)[1]);
        } catch (IndexOutOfBoundsException | JSONException e) {
            System.out.println("wrong command");
        }
    }

    private static void createLobby(PrintWriter out, String lobbyName) throws JSONException {
        JSONObject lobbyRequest = new JSONObject();
        lobbyRequest.put("type", "create_lobby");
        lobbyRequest.put("name", lobbyName);
        out.println(lobbyRequest.toString());
    }

    private static void listLobbies(PrintWriter out) throws JSONException {
        JSONObject lobbyRequest = new JSONObject();
        lobbyRequest.put("type", "list_lobbies");
        out.println(lobbyRequest.toString());
    }

    private static void joinLobby(PrintWriter out, String lobbyName) throws JSONException {
        JSONObject lobbyRequest = new JSONObject();
        lobbyRequest.put("type", "join_lobby");
        lobbyRequest.put("name", lobbyName);
        out.println(lobbyRequest.toString());
    }

    private static void leaveLobby(PrintWriter out, String lobbyName) throws JSONException {
        JSONObject lobbyRequest = new JSONObject();
        lobbyRequest.put("type", "leave_lobby");
        lobbyRequest.put("name", lobbyName);
        out.println(lobbyRequest.toString());
    }
    public static void playCard(PrintWriter out,String command) throws JSONException {
        JSONObject lobbyRequest = new JSONObject();
        lobbyRequest.put("type", "play");
        lobbyRequest.put("card", command.split(" ")[1]);
        out.println(lobbyRequest.toString());
    }


}
