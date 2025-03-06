package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements DataAccess {
    Map<String, AuthData> allAuths;

    public MemoryAuthDAO() {
        allAuths = new HashMap<>();
    }

    public void createAuth(AuthData authData) {
        allAuths.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!allAuths.containsKey(authToken)) {
            throw new DataAccessException("Unauthorized logout request");
        }
        return allAuths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        allAuths.remove(authToken);
    }

    public Map<String, AuthData> getAllAuths() {
        return allAuths;
    }

    @Override
    public void clear() {
        allAuths.clear();
    }
}
