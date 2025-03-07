package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
