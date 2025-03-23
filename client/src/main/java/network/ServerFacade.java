package network;

import execption.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
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

//    public String getURL() {
//        return serverUrl;
//    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, IOException {
        try {
            RegisterResult result = clientCommunicator.register(request);
            return result;
        } catch (ResponseException e) {
            if (e.StatusCode() == 401) {
                return new RegisterResult("401", "");
            } else if (e.StatusCode() == 403) {
                return new RegisterResult("403", "");
            } else {
                return new RegisterResult("500", "");
            }
        }

        //return clientCommunicator.register(request);
        //return new RegisterResult("a", "a");
    }

    public LoginResult login(LoginRequest request) {
        try {
            LoginResult result = clientCommunicator.login(request);
            return result;
        } catch (ResponseException e) {
            if (e.StatusCode() == 401) {
                return new LoginResult("401", "");
            } else {
                return new LoginResult("500", "");
            }
        }
    }
}
