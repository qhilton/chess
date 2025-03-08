package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    SQLUserDAO user; //change to SQLUserDAO
    SQLAuthDAO auth; //change to SQLAuthDAO

//    public UserService() throws ResponseException, DataAccessException {
//        user = new SQLUserDAO();
//        auth = new SQLAuthDAO();
//    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, ResponseException {
        user = new SQLUserDAO();
        auth = new SQLAuthDAO();

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

//    public SQLUserDAO getUserDAO() {
//        return user;
//    }
//
    public SQLAuthDAO getAuthDAO() {
        return auth;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        //var hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (username == null || password == null) {
            throw new DataAccessException("Bad request");
        }
        else if (!user.authorizedUser(username, password)) {
            throw new DataAccessException("Unauthorized login request");
        }

        String authToken = UUID.randomUUID().toString();
        auth.createAuth(new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Unauthorized logout request");
        }

        try {
            auth.getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized logout request");
        }

        auth.deleteAuth(authToken);



    }
}
