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
        if (allUsers.get(username) == null) {
            throw new DataAccessException("User not found: " + username);
        }
        return allUsers.get(username);
    }

    public Map<String, UserData> getAllUsers() {
        return allUsers;
    }

    public Boolean unauthorizedUser(String username, String password) {
        return (!allUsers.containsKey(username) || !allUsers.get(username).password().equals(password));
    }

    @Override
    public void clear() {
        allUsers.clear();
    }
}
