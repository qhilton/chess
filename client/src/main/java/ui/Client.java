package ui;


import chess.ChessGame;
import execption.ResponseException;
import network.ServerFacade;
import request.RegisterRequest;
import result.RegisterResult;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    static Scanner scanner = new Scanner(System.in);
    static boolean loop = true;
    static String menu = "";
    static String serverUrl = "http://localhost:8080/";
    static ServerFacade server;

    public static void main(String[] args) throws IOException, ResponseException {
        if (args.length == 1) {
            serverUrl = args[0];
        }
        server = new ServerFacade(serverUrl);

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

        //call registration
        RegisterResult result = server.register(new RegisterRequest(username, password, email));
        if (result.authToken() != "") {
            System.out.println("Successfully registered " + username);
            menu = "auth";
        }

        else {
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

        //call login

        System.out.println("Successfully logged in " + username);
        menu = "auth";
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

        //call createGame

        System.out.println("Successfully created new game " + gameName);
    }

    private static void playGame() {
        System.out.println("Joining game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();

        //call joinGame

        System.out.println("Successfully joined game " + gameID);
        menu = "game";
    }

    private static void observeGame() {
        System.out.println("Observing game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();

        //call joinGame

        System.out.println("Successfully observing game " + gameID);
        menu = "game";
    }

    private static void listGames() {
        System.out.println("Listing games");

        //call listGames
        //print list of games
    }

    private static void logout() {
        System.out.println("Logging out");

        //call logout

        System.out.println("Successfully logged out");
        menu = "unauth";
    }

    private static void gamePlayMenu() {
//        System.out.println("Game");
        DrawChessBoard.drawChessBoard(ChessGame.TeamColor.WHITE);
        System.out.println("");
        DrawChessBoard.drawChessBoard(ChessGame.TeamColor.BLACK);
        loop = false;
    }
}


