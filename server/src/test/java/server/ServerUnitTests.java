package server;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUnitTests {

    private RegisterRequest registerRequest;
    private UserService userService;
    private GameService gameService;


    @BeforeEach
    public void init() {
        registerRequest = new RegisterRequest("myUserName", "myPassword", "myEmail");
        userService = new UserService();
        gameService = new GameService();
    }


    @BeforeEach
    public void setup() {
        userService.clear();
    }

    @Test
    public void registerPositiveTest() throws Exception {
        RegisterResult result = userService.register(registerRequest);

        assertEquals("myUserName", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegativeTest() throws DataAccessException {
        userService.register(registerRequest);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(registerRequest);
        });

        assertEquals("User already exists: myUserName", exception.getMessage());
    }

    @Test
    public void userServiceClearTest() throws DataAccessException {
        userService.register(registerRequest);

        assertFalse(userService.getUserDAO().getAllUsers().isEmpty());
        assertFalse(userService.getAuthDAO().getAllAuths().isEmpty());

        userService.clear();

        assertTrue(userService.getUserDAO().getAllUsers().isEmpty());
        assertTrue(userService.getAuthDAO().getAllAuths().isEmpty());

    }

    @Test
    public void loginPositiveTest() throws DataAccessException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult result = userService.login(loginRequest);

        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegativeTest() throws DataAccessException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "notMyPassword");

        assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    public void logoutPositiveTest() throws DataAccessException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult result = userService.login(loginRequest);
        userService.logout(result.authToken());

        assertThrows(DataAccessException.class, () -> {
                userService.getAuthDAO().getAuth(result.authToken());
        });
    }

    @Test
    public void logoutNegativeTest() throws DataAccessException {
        userService.register(registerRequest);

        assertThrows(DataAccessException.class, () -> {
            userService.logout("authToken");
        });
    }

    @Test
    public void createGamePositiveTest() throws DataAccessException {
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myUserName", "myPassword");
        LoginResult loginResult = userService.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResult createGameResult = gameService.createGame(loginResult.authToken(), createGameRequest, userService);

        assertTrue(createGameResult.gameID() > 0);

//        assertThrows(DataAccessException.class, () -> {
//            userService.getAuthDAO().getAuth(loginResult.authToken());
//        });
    }

}
