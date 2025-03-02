package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    UserDAO user = new UserDAO();
    AuthDAO auth = new AuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        try {
            user.getUser(registerRequest.username());
            user.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
            auth.createAuth(new AuthData(authToken, registerRequest.username()));
            return new RegisterResult(registerRequest.username(), authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("User already exists: " + registerRequest.username());

        }

    }

    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}
