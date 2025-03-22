package client;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import execption.ResponseException;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import server.Server;

import java.io.IOException;
import java.net.MalformedURLException;


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
        RegisterRequest registerRequest = new RegisterRequest(null, "myPassword", "myEmail");
        Assertions.assertTrue(facade.register(registerRequest).authToken().equals(""));
    }

}
