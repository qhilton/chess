package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUnitTests {
    private SQLUserDAO user;
    private SQLAuthDAO auth;
    private SQLGameDAO game;
    private UserData userData;
    private AuthData authData;
    private GameData gameData;


    @BeforeEach
    public void init() throws ResponseException, DataAccessException {
        user = new SQLUserDAO();
        auth = new SQLAuthDAO();
        game = new SQLGameDAO();
        userData = new UserData("myUsername", "myPassword", "myEmail");
        authData = new AuthData("myAuthToken", "myUsername");
        gameData = new GameData(1, "white", "black", "myGame", new ChessGame());
    }


    @BeforeEach
    public void setup() {
        user.clear();
        auth.clear();
        game.clear();
    }



    /*** SQLUserDAO Unit Tests ***/

    @Test
    public void createUserPositiveTest() throws Exception {
        user.createUser(userData);
        String username = user.getUser("myUsername").username();

        assertEquals("myUsername", username);
    }

    @Test
    public void createUserNegativeTest() throws Exception {
        user.createUser(userData);

        assertThrows(DataAccessException.class, () -> {
            user.createUser(userData);
        });
    }

    @Test
    public void getUserPositiveTest() throws Exception {
        user.createUser(userData);
        String username = user.getUser("myUsername").username();

        assertEquals("myUsername", username);
    }

    @Test
    public void getUserNegativeTest() throws Exception {
        user.createUser(userData);

        assertThrows(DataAccessException.class, () -> {
            user.getUser("invalidUsername").username();
        });
    }

    @Test
    public void authorizedUserPositiveTest() throws Exception {
        user.createUser(userData);

        assertTrue(user.authorizedUser("myUsername", "myPassword"));
    }

    @Test
    public void authorizedUserNegativeTest() throws Exception {
        user.createUser(userData);

        assertFalse(user.authorizedUser("myUsername", "invalidPassword"));
    }

    @Test
    public void clearUserPositiveTest() throws Exception {
        user.createUser(userData);
        user.clear();

        assertThrows(DataAccessException.class, () -> {
            user.getUser("myUserName");
        });
    }


    /*** SQLAuthDAO Unit Tests ***/

    @Test
    public void createAuthPositiveTest() throws Exception {
        auth.createAuth(authData);
        String authToken = auth.getAuth("myAuthToken").authToken();

        assertEquals("myAuthToken", authToken);
    }

    @Test
    public void createAuthNegativeTest() throws Exception {
        auth.createAuth(authData);

        assertThrows(DataAccessException.class, () -> {
            auth.createAuth(authData);
        });
    }

    @Test
    public void getAuthPositiveTest() throws Exception {
        auth.createAuth(authData);
        String authToken = auth.getAuth("myAuthToken").authToken();

        assertEquals("myAuthToken", authToken);
    }

    @Test
    public void getAuthNegativeTest() throws Exception {
        auth.createAuth(authData);

        assertThrows(DataAccessException.class, () -> {
            auth.getAuth("invalidAuthToken").authToken();
        });
    }

    @Test
    public void deleteAuthPositiveTest() throws Exception {
        auth.createAuth(authData);
        String authToken = auth.getAuth("myAuthToken").authToken();

        assertEquals("myAuthToken", authToken);

        auth.deleteAuth(authToken);

        assertThrows(DataAccessException.class, () -> {
            auth.getAuth("myAuthToken").authToken();
        });
    }

    @Test
    public void deleteAuthNegativeTest() throws Exception {
        auth.createAuth(authData);
        String authToken = auth.getAuth("myAuthToken").authToken();
        auth.deleteAuth("invalidAuthToken");

        assertEquals("myAuthToken", authToken);
    }

    @Test
    public void clearAuthPositiveTest() throws Exception {
        auth.createAuth(authData);
        auth.clear();

        assertThrows(DataAccessException.class, () -> {
            auth.getAuth("myAuthToken");
        });
    }



    /*** SQLGameDAO Unit Tests ***/

    @Test
    public void createGamePositiveTest() throws Exception {
        game.createGame(gameData);
        String gameName = game.getGame(1).gameName();

        assertEquals("myGame", gameName);
    }

    @Test
    public void createGameNegativeTest() throws Exception {
        game.createGame(gameData);

        assertThrows(DataAccessException.class, () -> {
            game.createGame(gameData);
        });
    }

    @Test
    public void getGamePositiveTest() throws Exception {
        game.createGame(gameData);
        GameData myData = game.getGame(1);

        assertEquals(gameData, myData);
    }

    @Test
    public void getGameNegativeTest() throws Exception {
        assertThrows(DataAccessException.class, () -> {
            game.getGame(1);
        });
    }

    @Test
    public void listGamesPositiveTest() throws Exception {
        game.createGame(gameData);
        gameData = new GameData(2, "white2", "black2", "myGame2", new ChessGame());
        game.createGame(gameData);
        Collection<GameData> myList = game.listGames();

        assertEquals(2, myList.size());
    }

    @Test
    public void listGamesNegativeTest() throws Exception {
        Collection<GameData> myList = game.listGames();

        assertEquals(0, myList.size());
    }

    @Test
    public void updateGamePositiveTest() throws Exception {
        game.createGame(gameData);
        gameData = new GameData(1, "white2", "black2", "myGame2", new ChessGame());
        game.updateGame(1, gameData);

        assertEquals("white2", game.getGame(1).whiteUsername());
    }

    @Test
    public void updateGameNegativeTest() throws Exception {
        game.createGame(gameData);
        assertThrows(DataAccessException.class, () -> {
            game.updateGame(2, gameData);
        });
    }

    @Test
    public void clearGamePositiveTest() throws Exception {
        game.createGame(gameData);
        game.clear();

        assertThrows(DataAccessException.class, () -> {
            game.getGame(1);
        });
    }
}
