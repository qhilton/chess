package handler;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

public class UserHandler {
    UserService userService = new UserService();

    public String handleRegister(String json) throws Exception {
        Gson serializer = new Gson();
        RegisterRequest registerRequest = serializer.fromJson(json, RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        return serializer.toJson(registerResult);
    }

    public void clear() {
        userService.clear();
    }


}
