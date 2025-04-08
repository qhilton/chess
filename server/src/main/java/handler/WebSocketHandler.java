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
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;
import java.util.HashMap;

@WebSocket
public class WebSocketHandler {

//    public HashMap<Integer, Session> sessions = new HashMap<>();
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
//                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
//                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (DataAccessException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws Exception {
//        try {
//            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
//
//            // Throws a custom UnauthorizedException. Yours may work differently.
//            //String username = getUsername(command.getAuthToken());
//            String username = Server.userHandler.userService.auth.getAuth(command.getAuthToken()).username();
//
//            saveSession(command.getGameID(), session);
//
//            switch (command.getCommandType()) {
//                case CONNECT -> connect(session, username, (ConnectCommand) command);
////                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
////                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
////                case RESIGN -> resign(session, username, (ResignCommand) command);
//            }
//        } catch (DataAccessException ex) {
//            // Serializes and sends the error message
//            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
//        }
//    }
//
    public void connect(Session session, String username, String commandMessage) throws IOException, DataAccessException {
//        connections.add(username, command.getGameID(), session);
//        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT)
//        ConnectCommand connectCommand = new ConnectCommand(command.getCommandType(), command.getAuthToken(), command.getGameID())
        ConnectCommand command = new Gson().fromJson(commandMessage, ConnectCommand.class);
        String message = "";
        if (!command.getPlayerColor().equals("")) {
            message = String.format("%s joined the game as " + command.getPlayerColor(), username);
        } else {
            message = String.format("%s is observing the game", username);
        }
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage);

        GameData gameData = Server.gameHandler.gameService.game.getGame(command.getGameID());
        LoadGameMessage load = new LoadGameMessage(gameData.game());
        sendMessage(session.getRemote(), load);
    }

    public void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        remote.sendString(new Gson().toJson(message));
    }

//    public void saveSession(int gameID, Session session) {
//        sessions.put(gameID, session);
//    }
}
