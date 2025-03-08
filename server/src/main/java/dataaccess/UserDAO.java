package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    Boolean authorizedUser(String username, String password) throws DataAccessException;
    void clear();
}
