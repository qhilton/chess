package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDAO implements DataAccess {
    Map<String, UserData> allUsers;

    public UserDAO() {
        allUsers = new HashMap<>();
    }

    public void createUser(UserData userData) throws DataAccessException {
        if (allUsers.containsKey(userData.username())) {
            throw new DataAccessException("User already exists with the username: ");
        }

        allUsers.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws DataAccessException{
        UserData x = allUsers.get(username);
        if (allUsers.get(username) != null) {
            throw new DataAccessException("User already exists: " + username);
        }
        return allUsers.get(username);
    }

    @Override
    public void clear() {
        allUsers = null;
    }
}
