package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

public class UserHandler {
    UserService userService = new UserService();
    Gson serializer = new Gson();

    public String handleRegister(String json) throws DataAccessException {
        RegisterRequest registerRequest = serializer.fromJson(json, RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        return serializer.toJson(registerResult);
    }

    public String handleLogin(String json) throws DataAccessException {
        LoginRequest loginRequest = serializer.fromJson(json, LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        return serializer.toJson(loginResult);
    }

    public void clear() {
        userService.clear();
    }


}
