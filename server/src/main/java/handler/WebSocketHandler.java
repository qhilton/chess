package handler;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = Server.userHandler.userService.auth.getAuth(command.getAuthToken()).username();

            connections.add(username, command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, message);
                case MAKE_MOVE -> makeMove(session, username, message);
                case LEAVE -> leaveGame(session, username, message);
                case RESIGN -> resign(session, username, message);
            }
        } catch (DataAccessException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    public void connect(Session session, String username, String commandMessage) throws IOException, DataAccessException {
        ConnectCommand command = new Gson().fromJson(commandMessage, ConnectCommand.class);

        try {
            GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
            LoadGameMessage load = new LoadGameMessage(gameData.game());
            sendMessage(session.getRemote(), load);
        } catch (DataAccessException ex) {
            throw new DataAccessException("invalid game id");
        }

        // FOR TESTING ONLY
        if (command.getPlayerColor().equals("") && (username.equals("white") || username.equals("white2"))) {
            command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.WHITE);
        } else if (command.getPlayerColor().equals("") && username.equals("black")) {
            command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.BLACK);
        }

        String message = "";
        if (!command.getPlayerColor().equals("")) {
            message = String.format("%s joined the game as " + command.getPlayerColor(), username);
        } else {
            message = String.format("%s is observing the game", username);
        }
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage, false);
    }

    public void makeMove(Session session, String username, String commandMessage) throws Exception {
        MakeMoveCommand command = new Gson().fromJson(commandMessage, MakeMoveCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        // FOR TESTING ONLY
        ChessMove move = command.getMove();
        if (command.getPlayerColor() == null && (username.equals("white") || username.equals("white2"))) {
            command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, ChessGame.TeamColor.WHITE);
        } else if (command.getPlayerColor() == null && username.equals("black")) {
            command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, ChessGame.TeamColor.BLACK);
        }

        String message = "";
        String startPosition = convertPosition(move.getStartPosition());
        String endPosition = convertPosition(move.getEndPosition());

        if (command.getPlayerColor() != null) {
            message = String.format("%s moved from " + startPosition + " to " + endPosition, username);
        } else {
            throw new Exception("observers cannot make moves");
        }

        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();

        ChessGame.TeamColor otherTeamColor = null;
        String otherColor = "";
        if (command.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            otherTeamColor = ChessGame.TeamColor.BLACK;
            otherColor = "black";
        } else if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            otherTeamColor = ChessGame.TeamColor.WHITE;
            otherColor = "white";
        }

        if (game.getLiveGame()) {
            try {
                if (game.getTeamTurn() != command.getPlayerColor()) {
                    throw new Exception("not your turn");
                }
                game.makeMove(move);

                if (game.isInStalemate(otherTeamColor) || game.isInCheckmate(otherTeamColor)) {
                    game.disableGame();
                }

                GameData newData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                Server.gameHandler.gameService.game.updateGame(gameID, newData);

                LoadGameMessage load = new LoadGameMessage(newData.game());
                sendMessage(session.getRemote(), load);
                connections.broadcast(username, load, false);
            } catch (DataAccessException ex) {
                throw new DataAccessException(ex.getMessage());
            } catch (InvalidMoveException e) {
                throw new Exception("not a valid move");
            }
        } else {
            throw new DataAccessException("game is already over");
        }


        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage, false);

        //add check stuff here
        if (otherTeamColor != null && game.isInCheckmate(otherTeamColor)) {
            message = String.format("%s is in checkmate!\n" + username + " wins!", otherColor);
            serverMessage = new NotificationMessage(message);
            connections.broadcast(username, serverMessage, true);
        } else if (otherTeamColor != null && game.isInCheck(otherTeamColor)) {
            message = String.format("%s is in check!", otherColor);
            serverMessage = new NotificationMessage(message);
            connections.broadcast(username, serverMessage, true);
        } else if (otherTeamColor != null && game.isInStalemate(otherTeamColor)) {
            message = "The game is in a stalemate!";
            serverMessage = new NotificationMessage(message);
            connections.broadcast(username, serverMessage, true);
        }
    }



    public void leaveGame(Session session, String username, String commandMessage) throws IOException, DataAccessException {
        LeaveGameCommand command = new Gson().fromJson(commandMessage, LeaveGameCommand.class);
        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        int gameID = gameData.gameID();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        String message = String.format("%s left the game", username);

        // FOR TESTING ONLY
        if (command.getPlayerColor().equals("") && (username.equals("white") || username.equals("white2"))) {
            command = new LeaveGameCommand(UserGameCommand.CommandType.LEAVE, command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.WHITE);
        } else if (command.getPlayerColor().equals("") && username.equals("black")) {
            command = new LeaveGameCommand(UserGameCommand.CommandType.LEAVE, command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.BLACK);
        }

        if (command.getPlayerColor().equals("white")) {
            String blackUsername = gameData.blackUsername();
            GameData newData = new GameData(gameID, null, blackUsername, gameName, game);
            Server.gameHandler.gameService.game.updateGame(gameID, newData);
        } else if (command.getPlayerColor().equals("black")) {
            String whiteUsername = gameData.whiteUsername();
            GameData newData = new GameData(gameID, whiteUsername, null, gameName, game);
            Server.gameHandler.gameService.game.updateGame(gameID, newData);
        }

        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage, false);
        connections.remove(username);
    }

    public void resign(Session session, String username, String commandMessage) throws Exception {
        ResignCommand command = new Gson().fromJson(commandMessage, ResignCommand.class);
        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();

        String message = "";

        // FOR TESTING ONLY
        if (command.getPlayerColor().equals("") && (username.equals("white") || username.equals("white2"))) {
            command = new ResignCommand(UserGameCommand.CommandType.RESIGN, command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.WHITE);
        } else if (command.getPlayerColor().equals("") && username.equals("black")) {
            command = new ResignCommand(UserGameCommand.CommandType.RESIGN, command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.BLACK);
        }

        if (command.getPlayerColor().equals("white")) {
            message = String.format("%s resigned from the game%nBlack wins!", username);
        } else if (command.getPlayerColor().equals("black")) {
            message = String.format("%s resigned from the game%nWhite wins!", username);
        } else {
            throw new Exception("observers are unable to resign");
        }

        if (game.getLiveGame()) {
            game.disableGame();
        } else {
            throw new Exception("game is already over");
        }

        GameData newData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        Server.gameHandler.gameService.game.updateGame(gameID, newData);

        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage, true);
    }

    public void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        remote.sendString(new Gson().toJson(message));
    }

    private static String convertPosition(ChessPosition chessPosition) {
        return convertCol(chessPosition.getColumn()) + convertRow(chessPosition.getRow());
    }

    private static String convertCol(int colPos) {
        String col = "";
        if (colPos == 1) {
            col = "a";
        } else if (colPos == 2) {
            col = "b";
        } else if (colPos == 3) {
            col = "c";
        } else if (colPos == 4) {
            col = "d";
        } else if (colPos == 5) {
            col = "e";
        } else if (colPos == 6) {
            col = "f";
        } else if (colPos == 7) {
            col = "g";
        } else if (colPos == 8) {
            col = "h";
        }
        return col;
    }

    private static String convertRow(int rowPos) {
        return String.valueOf(rowPos);
    }
}
