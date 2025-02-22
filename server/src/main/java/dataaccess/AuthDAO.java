package dataaccess;

import model.AuthData;

public class AuthDAO implements DataAccess {
    AuthData authData;

    public void createAuth(String authToken, String username) {
        authData = new AuthData(authToken, username);
    }

    @Override
    public void clear() {
        authData = null;
    }

    public AuthData getAuth() {
        return authData;
    }
}
