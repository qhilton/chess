package network;

import execption.ResponseException;
import model.GameData;
import request.*;
import result.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;
    private ClientCommunicator clientCommunicator;

    public ServerFacade(String url) throws MalformedURLException {
        serverUrl = url;
        clientCommunicator = new ClientCommunicator(serverUrl);
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, IOException {
        try {
            return clientCommunicator.register(request);
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
            return clientCommunicator.login(request);
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
            return clientCommunicator.logout(request);
        } catch (ResponseException e) {
            return new LogoutResult(e.getStatusCode());
        }
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        try {
            return clientCommunicator.createGame(request, authToken);
        } catch (ResponseException e) {
            if (e.getStatusCode() == 401) {
                return new CreateGameResult(401);
            }
            return new CreateGameResult(e.getStatusCode());
        }
    }

    public ListGamesResult listGames(String authToken) {
        try {
            return clientCommunicator.listGames(authToken);
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
            return clientCommunicator.joinGame(request, authToken);
        } catch (ResponseException e) {
            return new LogoutResult(e.getStatusCode());
        }
    }
}
