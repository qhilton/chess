package ui;

import chess.*;
import chess.PawnMovesCalculator;
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
import java.util.concurrent.CompletableFuture;

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
    static Boolean liveGame = true;
    static int idKey;
    static ChessGame game = new ChessGame();
    static ChessGame.TeamColor teamColor;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl, this);
        gameIDs = new HashMap<>();
    }

    public static void runMenu() throws ResponseException, Exception {

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
        System.out.println("\nJoining game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter team color (w/b)");
        playerColor = scanner.nextLine();
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

    private static void observeGame() throws Exception {
        listGames();
        System.out.println("Observing game");
        System.out.println("Enter game ID");
        int gameID = scanner.nextInt();
        scanner.nextLine();
        playerColor = "Observe";

        idKey = updateGameID(gameID);
        if (idKey != 500) {
            server.makeConnection();
            server.notifyObserve(authToken, gameIDs.get(idKey));

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
        } else {
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

    private static void gamePlayMenu() throws Exception {
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
                makeMove();
                break;
            case ("3"):
                highlightMoves();
                break;
            case ("4"):
                leaveGame();
                break;
            case ("5"):
                resign();
                break;
        }
    }

    private static void makeMove() throws Exception {
        if (game.getLiveGame()) {
            System.out.println("Making a move.");
            System.out.println("Enter starting position (Ex d2)");
            String start = scanner.nextLine();
            System.out.println("Enter ending position (Ex d4)");
            String end = scanner.nextLine();
            ChessPosition startPosition = validatePiece(start);
            if (startPosition == null) {
                System.out.println("Invalid starting input. Please try again.");
            }
            ChessPosition endPosition = validatePiece(end);
            if (endPosition == null) {
                System.out.println("Invalid ending input. Please try again.");
            }
            if (startPosition != null && endPosition != null) {
                handleMove(startPosition, endPosition);
            }
        } else {
            System.out.println("Game is over. Cannot make a move.");
        }
    }

    private static void handleMove(ChessPosition startPosition, ChessPosition endPosition) throws Exception {
        if (game.getBoard().getPiece(startPosition) != null) {
            if (teamColor == ChessGame.TeamColor.WHITE) {
                if (game.getBoard().getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) && endPosition.getRow() == 8) {
                    setPromotionPiece(startPosition, endPosition);
                } else {
                    ChessMove move = new ChessMove(startPosition, endPosition, null);
                    server.notifyMove(authToken, gameIDs.get(idKey), move, teamColor);
                }
            } else if (teamColor == ChessGame.TeamColor.BLACK) {
                if (game.getBoard().getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) && endPosition.getRow() == 1) {
                    setPromotionPiece(startPosition, endPosition);
                } else {
                    ChessMove move = new ChessMove(startPosition, endPosition, null);
                    server.notifyMove(authToken, gameIDs.get(idKey), move, teamColor);
                }
            } else {
                System.out.println("Observers cannot make a move.");
            }
        } else {
            System.out.println("Error: not a valid move");
        }
    }

    private static void setPromotionPiece(ChessPosition startPosition, ChessPosition endPosition) throws Exception {
        System.out.println("Enter pawn promotion piece (Q, R, B, N)");
        String promo = scanner.nextLine();
        ChessPiece promotionPiece = validPromoInput(promo);
        if (promotionPiece.getPieceType() == null) {
            System.out.println("Invalid promotion input. Please try again.");
        } else {
            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece.getPieceType());
            server.notifyMove(authToken, gameIDs.get(idKey), move, teamColor);
        }
    }

    private static ChessPiece validPromoInput(String input) {
        if (input.equals("Q")) {
            return new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
        } else if (input.equals("R")) {
            return new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
        } else if (input.equals("B")) {
            return new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        } else if (input.equals("N")) {
            return new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        } else {
            return new ChessPiece(teamColor, null);
        }
    }

    private static void drawBoard(ChessPosition position) {
        if (playerColor.equals("WHITE") || playerColor.equals("Observe")) {
            DrawChessBoard.drawChessBoard(game, ChessGame.TeamColor.WHITE, position);
            System.out.println("");
        } else if (playerColor.equals("BLACK")) {
            DrawChessBoard.drawChessBoard(game, ChessGame.TeamColor.BLACK, position);
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

    private static void leaveGame() throws Exception {
        System.out.println("Are you sure you want to leave the game? (y/n)");
        String confirmLeave = scanner.nextLine();
        if (confirmLeave.equals("y")) {
            System.out.println("Leaving game.");
            server.notifyLeave(authToken, gameIDs.get(idKey), teamColor);
            menu = "auth";
        } else if (confirmLeave.equals("n")) {
            System.out.println("Staying in game.");
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void resign() throws Exception {
        System.out.println("Are you sure you want to resign from the game? (y/n)");
        String confirmLeave = scanner.nextLine();
        if (confirmLeave.equals("y")) {
            System.out.println("Resigning from the game.");
            liveGame = false;
            server.notifyResign(authToken, gameIDs.get(idKey), teamColor);
        } else if (confirmLeave.equals("n")) {
            System.out.println("Staying in game.");
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static ChessPosition validatePiece(String positionString) {
        String colPos = "";
        String rowPos = "";
        if (positionString.length() > 1) {
            colPos = positionString.substring(0, 1);
            rowPos = positionString.substring(1, 2);
        }
        int row = convertRow(rowPos);
        int col = convertCol(colPos);
        if (row == 0 || col == 0) {
            return null;
        }
        return new ChessPosition(row, col);
    }

    private static int convertCol(String colPos) {
        int col = 0;
        if (colPos.length() == 1 && colPos.charAt(0) >= 'a' && colPos.charAt(0) <= 'h') {
            col = colPos.charAt(0) - 'a' + 1;
        }
        return col;
    }

    private static int convertRow(String rowPos) {
        int row = 0;
        try {
            int parsedRow = Integer.parseInt(rowPos);
            if (parsedRow >= 1 && parsedRow <= 8) {
                row = parsedRow;
            }
        } catch (NumberFormatException e) {
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
        System.out.println("");
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private void displayNotification(String message) {
        System.out.println(message);
    }

    private void displayError(String message) {
        System.out.println(message);
    }

    private void loadGame(ChessGame newGame) {
        game = newGame;
        if (game.getLiveGame()) {
            drawBoard(new ChessPosition(0, 0));
        } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            drawBoard(new ChessPosition(0, 0));
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            drawBoard(new ChessPosition(0, 0));
        }
    }
}