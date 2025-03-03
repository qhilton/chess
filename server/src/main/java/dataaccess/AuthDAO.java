package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO implements DataAccess {
    Map<String, AuthData> allAuths;

    public AuthDAO() {
        allAuths = new HashMap<>();
    }

    public void createAuth(AuthData authData) {
        allAuths.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) {
        return allAuths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        allAuths.remove(authToken);
    }

    public Map getAllAuths() {
        return allAuths;
    }

    @Override
    public void clear() {
        allAuths.clear();
    }
}
