package dataaccess;

import model.GameData;
import model.UserData;

public class UserDAO implements DataAccess {
    UserData userData;

    //@Override
    public void createUser(String username, String password, String email) {
        userData = new UserData(username, password, email);
    }

    @Override
    public void clear() {
        userData = null;
    }

    //@Override
    public UserData getUser() {
        return userData;
    }

    //@Override
    public GameData getGame() {
        return null;
    }
}
