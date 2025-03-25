package client;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import execption.ResponseException;
import model.GameData;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LogoutResult;
import server.Server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    private static SQLUserDAO user;
    private static SQLAuthDAO auth;
    private static SQLGameDAO game;

    @BeforeAll
    public static void init() throws MalformedURLException, ResponseException, DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);

        user = new SQLUserDAO();
        auth = new SQLAuthDAO();
        game = new SQLGameDAO();
    }

    @AfterAll
    static void stopServer() {
        user.clear();
        auth.clear();
        game.clear();
        server.stop();
    }

    @BeforeEach
    public void setup() {
        user.clear();
        auth.clear();
        game.clear();
    }


    @Test
    public void positiveRegisterTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        Assertions.assertTrue(!facade.register(registerRequest).authToken().equals(""));
    }

    @Test
    public void negativeRegisterTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("", "myPassword", "myEmail");
        Assertions.assertTrue(facade.register(registerRequest).authToken().equals(""));
    }

    @Test
    public void positiveLoginTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        LoginRequest loginRequest = new LoginRequest("myUsername", "myPassword");
        String authToken = facade.register(registerRequest).authToken();
        facade.logout(new LogoutRequest(authToken)); // change to logout
        Assertions.assertTrue(!facade.login(loginRequest).authToken().equals(""));
    }

    @Test
    public void negativeLoginTest() throws ResponseException, IOException {
        LoginRequest loginRequest = new LoginRequest("myUsername", "myPassword");
        Assertions.assertTrue(facade.login(loginRequest).authToken().equals(""));
    }

    @Test
    public void positiveLogoutTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        String authToken = facade.register(registerRequest).authToken();

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult result = facade.logout(logoutRequest);
        Assertions.assertTrue(result.status() == 0);
    }

    @Test
    public void negativeLogoutTest() throws ResponseException, IOException {
        LogoutRequest logoutRequest = new LogoutRequest("");
        Assertions.assertTrue(facade.logout(logoutRequest).status() == 401);
    }

    @Test
    public void positiveCreateGameTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        String authToken = facade.register(registerRequest).authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult result = facade.createGame(createGameRequest, authToken);

        Assertions.assertTrue(result.gameID() != 401);
        Assertions.assertTrue(result.gameID() != 500);
    }

    @Test
    public void negativeCreateGameTest() throws ResponseException, IOException {
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult result = facade.createGame(createGameRequest, "");

        Assertions.assertTrue(result.gameID() == 401);
    }

    @Test
    public void positiveListGamesTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        String authToken = facade.register(registerRequest).authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        facade.createGame(createGameRequest, authToken);
        ListGamesResult result = facade.listGames(authToken);
        ArrayList<GameData> data = (ArrayList) result.games();

        Assertions.assertTrue(data.get(0).gameID() != 401);
        Assertions.assertTrue(data.get(0).gameID() == 1);
    }

    @Test
    public void negativeListGamesTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        String authToken = facade.register(registerRequest).authToken();
//        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
//        facade.createGame(createGameRequest, authToken);
        ListGamesResult result = facade.listGames("");
        ArrayList<GameData> data = (ArrayList) result.games();

        Assertions.assertEquals(401, data.get(0).gameID());
    }

    @Test
    public void positiveJoinGameTest() throws ResponseException, IOException {
        game.clear();
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        String authToken = facade.register(registerRequest).authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        int id = facade.createGame(createGameRequest, authToken).gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", id);
        LogoutResult result = facade.joinGame(joinGameRequest, authToken);
        //ArrayList<GameData> data = (ArrayList) result.games();

        Assertions.assertEquals(0, result.status());
    }

    @Test
    public void negativeJoinGameTest() throws ResponseException, IOException {
        RegisterRequest registerRequest = new RegisterRequest("myUsername", "myPassword", "myEmail");
        String authToken = facade.register(registerRequest).authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        facade.createGame(createGameRequest, authToken);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        LogoutResult result = facade.joinGame(joinGameRequest, "");
        //ArrayList<GameData> data = (ArrayList) result.games();

        Assertions.assertEquals(result.status(), 401);
    }

}
