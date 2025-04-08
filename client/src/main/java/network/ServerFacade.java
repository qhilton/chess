package network;

import chess.ChessGame;
import com.google.gson.Gson;
import execption.ResponseException;
import model.GameData;
import request.*;
import result.*;
import ui.Client;
import ui.ServerMessageObserver;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;
    private HttpCommunicator httpCommunicator;
    private WebSocketCommunicator webSocketCommunicator;
    private ServerMessageObserver observer;

    public ServerFacade(String url, ServerMessageObserver observer) throws Exception {
        serverUrl = url;
        httpCommunicator = new HttpCommunicator(serverUrl);
        this.observer = observer;
        //webSocketCommunicator = new WebSocketCommunicator(serverUrl, observer);
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, IOException {
        try {
            return httpCommunicator.register(request);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 401) {
                return new RegisterResult("401", "");
            } else if (e.getStatusCode() == 403) {
                return new RegisterResult("403", "");
            } else {
                return new RegisterResult("500", "");
            }
        }
    }

    public LoginResult login(LoginRequest request) {
        try {
            return httpCommunicator.login(request);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 401) {
                return new LoginResult("401", "");
            } else {
                return new LoginResult("500", "");
            }
        }
    }

    public LogoutResult logout(LogoutRequest request) {
        try {
            return httpCommunicator.logout(request);
        } catch (ResponseException e) {
            return new LogoutResult(e.getStatusCode());
        }
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        try {
            return httpCommunicator.createGame(request, authToken);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 401) {
                return new CreateGameResult(401);
            }
            return new CreateGameResult(e.getStatusCode());
        }
    }

    public ListGamesResult listGames(String authToken) {
        try {
            return httpCommunicator.listGames(authToken);
        } catch (ResponseException e) {
            Collection<GameData> list = new ArrayList<>();
            if (e.getStatusCode() == 401) {
                list.add(new GameData(401, "", "", "", null));
                return new ListGamesResult(list);
            }
            list.add(new GameData(500, "", "", "", null));
            return new ListGamesResult(list);
        }
    }

    public LogoutResult joinGame(JoinGameRequest request, String authToken) {
        try {
            return httpCommunicator.joinGame(request, authToken);
        } catch (ResponseException e) {
            return new LogoutResult(e.getStatusCode());
        }
    }

    public void makeConnection() throws Exception {
        webSocketCommunicator = new WebSocketCommunicator(serverUrl, observer);
    }

    public void notifyJoin(String authToken, int gameID, ChessGame.TeamColor playerColor) throws Exception {
        ConnectCommand command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, playerColor);
        webSocketCommunicator.send(new Gson().toJson(command));
    }

    public void notifyObserve(String authToken, int gameID) throws Exception {
        ConnectCommand command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, null);
        webSocketCommunicator.send(new Gson().toJson(command));
    }

    public void notifyLeave(String authToken, int gameID, ChessGame.TeamColor playerColor) throws Exception {
        LeaveGameCommand command = new LeaveGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, playerColor);
        webSocketCommunicator.send(new Gson().toJson(command));
    }
}
