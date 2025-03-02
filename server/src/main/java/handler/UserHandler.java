package handler;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

public class UserHandler {
    public String handleRegister(String json) throws Exception {
        Gson serializer = new Gson();
        RegisterRequest registerRequest = serializer.fromJson(json, RegisterRequest.class);
        UserService userService = new UserService();
        RegisterResult registerResult = userService.register(registerRequest);
        return serializer.toJson(registerResult);
    }


}
