package ui;

import chess.ChessGame;
import execption.ResponseException;
import model.GameData;
import network.ServerFacade;
import request.*;
import result.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    static Scanner scanner = new Scanner(System.in);
    static boolean loop = true;
    static String menu = "";
    static String serverUrl = "http://localhost:8080/";
    static ServerFacade server;
    static String authToken;
    static ArrayList<GameData> data;
    static Map<Integer, Integer> gameIDs;
    static String playerColor;

    public static void main(String[] args) throws IOException, ResponseException {
        if (args.length == 1) {
            serverUrl = args[0];
        }
        server = new ServerFacade(serverUrl);
        gameIDs = new HashMap<>();

        System.out.println("Welcome to Chess!");
        menu = "unauth";
        while (loop) {
            switch (menu) {
                case ("unauth"):
                    unauthorizedMenu();
                    break;
                case ("auth"):
                    authorizedMenu();
                    break;
                case ("game"):
                    gamePlayMenu();
                    break;
            }
        }
    }


    private static void unauthorizedMenu() throws ResponseException, IOException {
        System.out.println("\nOptions");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Quit");
        System.out.println("4. Help");

        String result = scanner.nextLine();

        switch (result) {
            case ("1"):
                register();
                break;
            case ("2"):
                login();
                break;
            case ("3"):
                System.out.println("Exiting program");
                loop = false;
                break;
        }
    }

    private static void register() throws ResponseException, IOException {
        System.out.println("Registering new user");
        System.out.println("Enter username");
        String username = scanner.nextLine();
        System.out.println("Enter password");
        String password = scanner.nextLine();
        System.out.println("Enter email");
        String email = scanner.nextLine();

        RegisterResult result = server.register(new RegisterRequest(username, password, email));
        if (result.authToken() != "") {
            authToken = result.authToken();
            System.out.println("Successfully registered " + username);
            menu = "auth";
        } else {
            if (result.username().equals("403")) {
                System.out.println("User already exists. Please try again.");
            } else if (result.username().equals("401")) {
                System.out.println("Invalid input. Please try again.");
            } else {
                System.out.println("Unexpected error. Please try again.");
            }
        }
    }

    private static void login() {
        System.out.println("Logging in");
        System.out.println("Enter username");
        String username = scanner.nextLine();
        System.out.println("Enter password");
        String password = scanner.nextLine();

        LoginResult result = server.login(new LoginRequest(username, password));
        if (result.authToken() != "") {
            authToken = result.authToken();
            System.out.println("Successfully logged in " + username);
            menu = "auth";
        } else {
            if (result.username().equals("401")) {
                System.out.println("Invalid input. Please try again.");
            } else {
                System.out.println("Unexpected error. Please try again.");
            }
        }
    }

    private static void authorizedMenu() {
        System.out.println("\nOptions");
        System.out.println("1. Create Game");
        System.out.println("2. Play Game");
        System.out.println("3. Observe Game");
        System.out.println("4. List Games");
        System.out.println("5. Logout");
        System.out.println("6. Help");

        String result = scanner.nextLine();

        switch (result) {
            case ("1"):
                createGame();
                break;
            case ("2"):
                playGame();
                break;
            case ("3"):
                observeGame();
                break;
            case ("4"):
                listGames();
                break;
            case ("5"):
                logout();
                break;
        }
    }

    private static void createGame() {
        System.out.println("Creating new game");
        System.out.println("Enter game name");
        String gameName = scanner.nextLine();

        CreateGameResult result = server.createGame(new CreateGameRequest(gameName), authToken);
        if (result.gameID() == 401) {
            System.out.println("Unauthorized request. Please try again.");
        } else if (result.gameID() == 500) {
            System.out.println("Unexpected error. Please try again.");
        } else {
            System.out.println("Successfully created new game " + gameName);
        }
    }

    private static void playGame() {
        listGames();

        System.out.println("Joining game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter team color (w/b)");
        playerColor = scanner.nextLine();
        if (playerColor.equals("w")) {
            playerColor = "WHITE";
        } else if (playerColor.equals("b")) {
            playerColor = "BLACK";
        }

        int idKey = updateGameID(gameID);
        if (idKey != 500) {
            LogoutResult result = server.joinGame(new JoinGameRequest(playerColor, gameIDs.get(idKey)), authToken);
            if (result.status() == 0) {
                System.out.println("Successfully joined game");
                menu = "game";
            } else {
                if (result.status() == 400) {
                    System.out.println("Invalid input. Please try again.");
                } else if (result.status() == 401) {
                    System.out.println("Unauthorized request. Please try again.");
                } else if (result.status() == 403) {
                    System.out.println("Color already taken. Please try again.");
                } else {
                    System.out.println("Unexpected error. Please try again.");
                }
            }
        } else {
            System.out.println("No game found with id " + gameID + ". Please try again.");
        }
    }

    private static void observeGame() {
        listGames();

        System.out.println("Observing game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();
        scanner.nextLine();

        playerColor = "WHITE";

        int idKey = updateGameID(gameID);
        if (idKey != 500) {
            System.out.println("Successfully observing game " + gameID);
            menu = "game";
        } else {
            System.out.println("No game found with id " + gameID + ". Please try again.");
        }
    }

    private static void listGames() {
        System.out.println("Listing games");

        ListGamesResult result = server.listGames(authToken);
        data = (ArrayList) result.games();
        if (data.size() == 0) {
            System.out.println("No games have been created. Create a game and try again.");
        } else if (data.get(0).gameID() == 401) {
            System.out.println("Unauthorized request. Please try again.");
        } else if (data.get(0).gameID() == 500) {
            System.out.println("Unexpected error. Please try again.");
        }
        else {
            for (int i = 0; i < data.size(); i++) {
                System.out.println(i+1 + ". " + data.get(i).gameName());
                gameIDs.put(i, data.get(i).gameID());
            }
        }
    }

    private static void logout() {
        System.out.println("Logging out");

        LogoutResult result = server.logout(new LogoutRequest(authToken));
        if (result.status() == 0) {
            System.out.println("Successfully logged out");
            menu = "unauth";
        }
        else {
            if (result.status() == 401) {
                System.out.println("Unauthorized request. Please try again.");
            } else {
                System.out.println("Unexpected error. Please try again.");
            }
        }
    }

    private static void gamePlayMenu() {
        if (playerColor.equals("WHITE")) {
            DrawChessBoard.drawChessBoard(ChessGame.TeamColor.WHITE);
            System.out.println("");
        }
        else if (playerColor.equals("BLACK")) {
            DrawChessBoard.drawChessBoard(ChessGame.TeamColor.BLACK);
            System.out.println("");
        }
        loop = false;
    }

    private static int updateGameID(int id) {
        if (id <= data.size() && id > 0) {
            return id-1;
        }
        return 500;
    }
}


