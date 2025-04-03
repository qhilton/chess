package ui;

import chess.ChessGame;
import chess.ChessPosition;
import execption.ResponseException;
import model.GameData;
import network.ServerFacade;
import request.*;
import result.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client implements ServerMessageObserver {
    static Scanner scanner = new Scanner(System.in);
    static boolean loop = true;
    static String menu = "";
    static String serverUrl = "http://localhost:8080/";
    static ServerFacade server;
    static String authToken;
    static ArrayList<GameData> data;
    static Map<Integer, Integer> gameIDs;
    static String playerColor;
    static Boolean firstJoin = true;
    static int idKey;

    public static void main(String[] args) throws Exception, ResponseException {
        if (args.length == 1) {
            serverUrl = args[0];
        }
        server = new ServerFacade(serverUrl, new Client());
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

    private static void authorizedMenu() throws Exception {
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

    private static void playGame() throws Exception {
        listGames();

        System.out.println("Joining game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter team color (w/b)");
        playerColor = scanner.nextLine();
        ChessGame.TeamColor teamColor = null;
        if (playerColor.equals("w")) {
            playerColor = "WHITE";
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (playerColor.equals("b")) {
            playerColor = "BLACK";
            teamColor = ChessGame.TeamColor.BLACK;
        }

        idKey = updateGameID(gameID);
        if (idKey != 500) {
            LogoutResult result = server.joinGame(new JoinGameRequest(playerColor, gameIDs.get(idKey)), authToken);
            if (result.status() == 0) {
                server.makeConnection();
                server.notifyJoin(authToken, gameIDs.get(idKey), teamColor);
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

        playerColor = "Observe";

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
                System.out.print(i+1 + ". " + data.get(i).gameName());
                if (data.get(i).whiteUsername() != null) {
                    System.out.print(" (White: " + data.get(i).whiteUsername());
                } else {
                    System.out.print(" (White: <open>");
                }
                if (data.get(i).blackUsername() != null) {
                    System.out.println(" | (Black: " + data.get(i).blackUsername() + ")");
                } else {
                    System.out.println(" | (Black: <open>)");
                }
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
        if (firstJoin) {
            drawBoard(new ChessPosition(0, 0));
            firstJoin = false;
        }
        //menu = "auth";

        System.out.println("\nOptions");
        System.out.println("1. Redraw Chess Board");
        System.out.println("2. Make Move");
        System.out.println("3. Highlight Legal Moves");
        System.out.println("4. Leave");
        System.out.println("5. Resign");
        System.out.println("6. Help");

        String result = scanner.nextLine();

        switch (result) {
            case ("1"):
                drawBoard(new ChessPosition(0, 0));
                break;
            case ("2"):
                System.out.println("not implemented");
                break;
            case ("3"):
                highlightMoves();
                break;
            case ("4"):
                leaveGame();
                break;
            case ("5"):
                System.out.println("not implemented");
                break;
        }

    }

    private static void drawBoard(ChessPosition position) {
        if (playerColor.equals("WHITE") || playerColor.equals("Observe")) {
            DrawChessBoard.drawChessBoard(ChessGame.TeamColor.WHITE, position);
            System.out.println("");
        }
        else if (playerColor.equals("BLACK")) {
            DrawChessBoard.drawChessBoard(ChessGame.TeamColor.BLACK, position);
            System.out.println("");
        }
    }

    private static void highlightMoves() {
        System.out.println("Enter position (Ex: a1)");
        String positionString = scanner.nextLine();
        if (validatePiece(positionString) != null) {
            ChessPosition position = validatePiece(positionString);
            drawBoard(position);
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void leaveGame() {
        System.out.println("Are you sure you want to leave the game? (y/n)");
        String confirmLeave = scanner.nextLine();
        if (confirmLeave.equals("y")) {
            System.out.println("Leaving game.");

            //update game to remove player color from game
            if (idKey != 500) {
                LogoutResult result = server.joinGame(new JoinGameRequest(null, gameIDs.get(idKey)), authToken);
                if (result.status() == 0) {
                    System.out.println("Successfully left game");
                    menu = "auth";
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
            }

            //menu = "auth";
        } else if (confirmLeave.equals("n")) {
            System.out.println("Staying in game.");
        } else {
            System.out.println("Invalid input. Please try again.");
        }

    }

    private static ChessPosition validatePiece(String positionString) {
        String colPos = positionString.substring(0, 1);
        String rowPos = positionString.substring(1, 2);
        int row = convertRow(rowPos);
        int col = convertCol(colPos);

        if (row == 0 || col == 0) {
            return null;
        }

        return new ChessPosition(row, col);
    }

    private static int convertCol(String colPos) {
        int col = 0;
        if (colPos.equals("a")) {
            col = 1;
        } else if (colPos.equals("b")) {
            col = 2;
        } else if (colPos.equals("c")) {
            col = 3;
        } else if (colPos.equals("d")) {
            col = 4;
        } else if (colPos.equals("e")) {
            col = 5;
        } else if (colPos.equals("f")) {
            col = 6;
        } else if (colPos.equals("g")) {
            col = 7;
        } else if (colPos.equals("h")) {
            col = 8;
        }
        return col;
    }

    private static int convertRow(String rowPos) {
        int row = 0;
        if (rowPos.equals("1")) {
            row = 1;
        } else if (rowPos.equals("2")) {
            row = 2;
        } else if (rowPos.equals("3")) {
            row = 3;
        } else if (rowPos.equals("4")) {
            row = 4;
        } else if (rowPos.equals("5")) {
            row = 5;
        } else if (rowPos.equals("6")) {
            row = 6;
        } else if (rowPos.equals("7")) {
            row = 7;
        } else if (rowPos.equals("8")) {
            row = 8;
        }
        return row;
    }

    private static int updateGameID(int id) {
        if (id <= data.size() && id > 0) {
            return id-1;
        }
        return 500;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private void displayNotification(String message) {

    }

}


