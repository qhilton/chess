package dataaccess;

import model.AuthData;

import java.util.Map;

public class AuthDAO implements DataAccess {
    Map<String, AuthData> allAuths;

    public void createAuth(String authToken, String username) {
        allAuths.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken) {
        return allAuths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        allAuths.remove(authToken);
    }

    @Override
    public void clear() {
        allAuths = null;
    }
}
