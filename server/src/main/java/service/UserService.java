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
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new DataAccessException("Bad request");
        }

        String authToken = UUID.randomUUID().toString();
        try {
            user.getUser(registerRequest.username());
        } catch (DataAccessException e) {
            user.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
            auth.createAuth(new AuthData(authToken, registerRequest.username()));
            return new RegisterResult(registerRequest.username(), authToken);
        }
        throw new DataAccessException("User already exists: " + registerRequest.username());

    }

    public void clear() {
        user.clear();
        auth.clear();
    }

    public UserDAO getUserDAO() {
        return user;
    }

    public AuthDAO getAuthDAO() {
        return auth;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null) {
            throw new DataAccessException("Bad request");
        }
        else if (user.unauthorizedUser(username, password)) {
            throw new DataAccessException("Unauthorized login request");
        }

        String authToken = UUID.randomUUID().toString();
        auth.createAuth(new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }



    //public void logout(LogoutRequest logoutRequest) {}
}
