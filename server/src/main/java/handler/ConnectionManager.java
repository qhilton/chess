package handler;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer> gameConnector = new ConcurrentHashMap<>();

    public void add(String username, int gameID, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
        gameConnector.put(username, gameID);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeUserName, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                Boolean sameGame = gameConnector.get(excludeUserName) == gameConnector.get(c.username);
                if (!c.username.equals(excludeUserName)) {
                    c.send(message.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}