package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    Map<String, AuthData> allAuths;

    public MemoryAuthDAO() {
        allAuths = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        allAuths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!allAuths.containsKey(authToken)) {
            throw new DataAccessException("Unauthorized logout request");
        }
        return allAuths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        allAuths.remove(authToken);
    }

    @Override
    public void clear() {
        allAuths.clear();
    }

    public Map<String, AuthData> getAllAuths() {
        return allAuths;
    }
}
