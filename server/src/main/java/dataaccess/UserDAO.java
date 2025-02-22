package dataaccess;

import model.UserData;
import java.util.Map;

public class UserDAO implements DataAccess {
    Map<String, UserData> allUsers;

    public void createUser(String username, String password, String email) {
        allUsers.put(username, new UserData(username, password, email));
    }

    public UserData getUser(String username) {
        return allUsers.get(username);
    }

    @Override
    public void clear() {
        allUsers = null;
    }
}
