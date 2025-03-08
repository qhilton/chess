package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import result.RegisterResult;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUnitTests {
    private SQLUserDAO user;
    private UserData userData;

    @BeforeEach
    public void init() throws ResponseException, DataAccessException {
        user = new SQLUserDAO();
        userData = new UserData("myUsername", "myPassword", "myEmail");
    }


    @BeforeEach
    public void setup() {
        user.clear();
    }

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
    public void clearPositiveTest() throws Exception {
        user.createUser(userData);
        user.clear();

        assertThrows(DataAccessException.class, () -> {
            user.getUser("myUserName");
        });
    }
}
