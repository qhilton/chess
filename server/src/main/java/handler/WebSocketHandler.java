package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
                case CONNECT -> connect(session, username, (ConnectCommand) command);
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
    public void connect(Session session, String username, ConnectCommand command) throws IOException {
//        connections.add(username, command.getGameID(), session);
        var message = String.format("%s joined the game as " + command.getPlayerColor(), username);
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage);
    }


    //
    public void sendMessage(RemoteEndpoint remote, ErrorMessage message) throws IOException {
        remote.sendString(new Gson().toJson(message));
    }

//    public void broadcast(String excludeVisitorName, NotificationMessage notification) throws IOException {
//        var removeList = new ArrayList<Connection>();
//        for (var c : connections.values()) {
//            if (c.session.isOpen()) {
//                if (!c.visitorName.equals(excludeVisitorName)) {
//                    c.send(notification.toString());
//                }
//            } else {
//                removeList.add(c);
//            }
//        }
//
//        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.remove(c.visitorName);
//        }
//    }

//    public void saveSession(int gameID, Session session) {
//        sessions.put(gameID, session);
//    }
}
