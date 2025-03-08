package service;

import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTests {

    private RegisterRequest registerRequest;
    private UserService userService;
    private GameService gameService;


    @BeforeEach
    public void init() throws ResponseException, DataAccessException {
        registerRequest = new RegisterRequest("myUserName", "myPassword", "myEmail");
        userService = new UserService();
        gameService = new GameService();
    }


    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        RegisterRequest setupRequest = new RegisterRequest("setupUserName", "setupPassword", "setupEmail");
        userService.register(setupRequest);
        userService.clear();
    }

    @Test
    public void registerPositiveTest() throws Exception, ResponseException {
        RegisterResult result = userService.register(registerRequest);

        assertEquals("myUserName", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegativeTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(registerRequest);
        });

        assertEquals("User already exists: myUserName", exception.getMessage());
    }

    @Test
    public void userServiceClearTest() throws DataAccessException, ResponseException {
        RegisterResult result = userService.register(registerRequest);

        userService.clear();

        assertThrows(DataAccessException.class, () -> {
            assertNull(userService.getAuthDAO().getAuth(result.authToken()));
        });


    }

    @Test
    public void loginPositiveTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult result = userService.login(loginRequest);

        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegativeTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "notMyPassword");

        assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    public void logoutPositiveTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult result = userService.login(loginRequest);
        userService.logout(result.authToken());

        assertThrows(DataAccessException.class, () -> {
                userService.getAuthDAO().getAuth(result.authToken());
        });
    }

    @Test
    public void logoutNegativeTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);

        assertThrows(DataAccessException.class, () -> {
            userService.logout("authToken");
        });
    }

    @Test
    public void createGamePositiveTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult = userService.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult createGameResult = gameService.createGame(loginResult.authToken(), createGameRequest, userService);

        assertTrue(createGameResult.gameID() > 0);
    }

    @Test
    public void createGameNegativeTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult = userService.login(loginRequest);
        userService.logout(loginResult.authToken());

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(loginResult.authToken(), createGameRequest, userService);
        });
    }

    @Test
    public void joinGamePositiveTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult = userService.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult createGameResult = gameService.createGame(loginResult.authToken(), createGameRequest, userService);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        gameService.joinGame(loginResult.authToken(), joinGameRequest, userService);

        assertTrue(createGameResult.gameID() > 0);
    }

    @Test
    public void joinGameNegativeTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest1 = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult1 = userService.login(loginRequest1);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult createGameResult = gameService.createGame(loginResult1.authToken(), createGameRequest, userService);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        gameService.joinGame(loginResult1.authToken(), joinGameRequest, userService);

        userService.register(new RegisterRequest("otherUserName", "myPassword", "email"));
        LoginRequest loginRequest2 = new LoginRequest("otherUserName", "myPassword");
        LoginResult loginResult2 = userService.login(loginRequest2);

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(loginResult2.authToken(), joinGameRequest, userService);
        });
    }

    @Test
    public void listGamesPositiveTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult = userService.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult createGameResult = gameService.createGame(loginResult.authToken(), createGameRequest, userService);

        Collection<GameData> games = (gameService.listGames(loginResult.authToken(), userService));
        assertEquals(1, games.size());
        assertNotNull(games);
    }

    @Test
    public void listGamesNegativeTest() throws DataAccessException, ResponseException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult = userService.login(loginRequest);

        Collection<GameData> games = (gameService.listGames(loginResult.authToken(), userService));
        assertEquals(0, games.size());
    }

}
