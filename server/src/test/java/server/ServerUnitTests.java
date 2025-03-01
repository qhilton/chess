package server;

import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUnitTests {
    @Test
    public void registerPositiveTest() throws Exception {
        RegisterRequest request = new RegisterRequest("myUserName", "myPassword", "myEmail");
        UserService userService = new UserService();
        RegisterResult result = userService.register(request);

        assertEquals("myUserName", result.username());
        assertNotNull(result.authToken());
    }
}
