package server;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUnitTests {

    private RegisterRequest request;
    private UserService userService;

    @BeforeEach
    public void init() {
        request = new RegisterRequest("myUserName", "myPassword", "myEmail");
        userService = new UserService();
    }


    @BeforeEach
    public void setup() {
        userService.clear();
    }

    @Test
    public void registerPositiveTest() throws Exception {
        RegisterResult result = userService.register(request);

        assertEquals("myUserName", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegativeTest() throws DataAccessException {
        userService.register(request);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });

//        assertEquals("User already exists: myUserName", exception.getMessage());
    }

    @Test
    public void userServiceClearTest() throws DataAccessException {
        userService.register(request);

        assertFalse(userService.getUserDAO().getAllUsers().isEmpty());
        assertFalse(userService.getAuthDAO().getAllAuths().isEmpty());

        userService.clear();

        assertTrue(userService.getUserDAO().getAllUsers().isEmpty());
        assertTrue(userService.getAuthDAO().getAllAuths().isEmpty());

    }
}
