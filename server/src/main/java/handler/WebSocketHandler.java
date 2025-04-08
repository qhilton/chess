package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import spark.Spark;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;
import java.util.HashMap;

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
//                case CONNECT -> connect(session, username, new Gson().fromJson(message, ConnectCommand.class));
//                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
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
        String message = "";
        if (!command.getPlayerColor().equals("")) {
            message = String.format("%s joined the game as " + command.getPlayerColor(), username);
        } else {
            message = String.format("%s is observing the game", username);
        }
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage, false);

        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        LoadGameMessage load = new LoadGameMessage(gameData.game());
        sendMessage(session.getRemote(), load);
    }

    public void leaveGame(Session session, String username, String commandMessage) throws IOException, DataAccessException {
        LeaveGameCommand command = new Gson().fromJson(commandMessage, LeaveGameCommand.class);
        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        int gameID = gameData.gameID();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        String message = String.format("%s left the game", username);

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
    }

    public void resign(Session session, String username, String commandMessage) throws IOException, DataAccessException {
        ResignCommand command = new Gson().fromJson(commandMessage, ResignCommand.class);
        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();

        String message = "";

        if (command.getPlayerColor().equals("white")) {
            message = String.format("%s resigned from the game%nBlack wins!", username);
        } else if (command.getPlayerColor().equals("black")) {
            message = String.format("%s resigned from the game%nWhite wins!", username);
        }

        game.disableGame();
        GameData newData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        Server.gameHandler.gameService.game.updateGame(gameID, newData);

        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage, true);
    }

    public void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        remote.sendString(new Gson().toJson(message));
    }

//    public void saveSession(int gameID, Session session) {
//        sessions.put(gameID, session);
//    }
}
