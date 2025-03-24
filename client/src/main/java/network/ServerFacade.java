package network;

import execption.ResponseException;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import java.io.IOException;
import java.net.MalformedURLException;

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
            if (e.StatusCode() == 401) {
                return new RegisterResult("401", "");
            } else if (e.StatusCode() == 403) {
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
            if (e.StatusCode() == 401) {
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
            return new LogoutResult(e.StatusCode());
        }
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        try {
            //LogoutResult result = clientCommunicator.logout(request);
            return clientCommunicator.createGame(request, authToken);
        } catch (ResponseException e) {
            if (e.StatusCode() == 401) {
                return new CreateGameResult(401);
            }// else {
//                return new LoginResult("500", "");
//            }
            return new CreateGameResult(e.StatusCode());
        }
    }
}
