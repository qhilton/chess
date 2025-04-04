package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import execption.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

public class UserHandler {
    UserService userService = new UserService();
    Gson serializer = new Gson();

    public String handleRegister(String json) throws DataAccessException, ResponseException {
        RegisterRequest registerRequest = serializer.fromJson(json, RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        return serializer.toJson(registerResult);
    }

    public String handleLogin(String json) throws DataAccessException, ResponseException {
        LoginRequest loginRequest = serializer.fromJson(json, LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        return serializer.toJson(loginResult);
    }

    public void handleLogout(String json) throws DataAccessException, ResponseException {
        userService.logout(serializer.fromJson(json, String.class));
    }

    public void clear() throws ResponseException, DataAccessException {
        userService.clear();
    }

    public UserService getUserService() {
        return userService;
    }


}
