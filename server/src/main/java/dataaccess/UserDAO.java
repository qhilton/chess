package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDAO implements DataAccess {
    Map<String, UserData> allUsers;

    public UserDAO() {
        allUsers = new HashMap<>();
    }

    public void createUser(UserData userData) {
//        if (allUsers.containsKey(username)) {
//            throw new DataAccessException("User already exists with the username: ");
//        }

        allUsers.put(userData.username(), userData);
    }

    public UserData getUser(String username) {
        return allUsers.get(username);
    }

    @Override
    public void clear() {
        allUsers = null;
    }
}
