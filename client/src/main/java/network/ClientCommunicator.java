package network;

import com.google.gson.Gson;
import execption.ResponseException;
import request.*;
import result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ClientCommunicator {
    //private ServerFacade server;
    URL url;
    String serverUrl;
    Gson serializer;
    String authToken;

    public ClientCommunicator(String urlString) throws MalformedURLException {
        //this.url = url.toURL();
        serverUrl = urlString;
        url = new URL(urlString);
        serializer = new Gson();
        authToken = new String();
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request) throws ResponseException {
        var path = "/session";
        this.authToken = request.authToken();
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        this.authToken = authToken;
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        this.authToken = authToken;
        return this.makeRequest("GET", path, null, ListGamesResult.class);
    }

    public LogoutResult joinGame(JoinGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        this.authToken = authToken;
        return this.makeRequest("PUT", path, request, LogoutResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (request != null && request instanceof LogoutRequest) {
                http.setRequestProperty("Authorization", new Gson().toJson(authToken));
            } else {
                http.setRequestProperty("Authorization", authToken);
            }
            if (request != null) {
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private String throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(status, respErr);

                }
            }
        }
        return "Success";
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
