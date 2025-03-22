package network;

import execption.ResponseException;
import request.RegisterRequest;
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
        return clientCommunicator.register(request);
        //return new RegisterResult("a", "a");
    }
}
