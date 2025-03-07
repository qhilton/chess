package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
//import exception.ResponseException;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserDAO {

    public SQLUserDAO() throws ResponseException, DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(2, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("User already exists with the username: " + userData.username());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        var password = result.getString("password");
                        var email = result.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found: " + username);
        }
    }

    public Boolean unauthorizedUser(String username, String password) throws DataAccessException {
        UserData userData = getUser(username);
        var hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return BCrypt.checkpw(hashedPassword, userData.password());
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE user";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            //throw new DataAccessException(e);
        }
    }



//    private int executeUpdate(String statement, Object... params) throws ResponseException {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param == null) ps.setNull(i + 1, NULL);
//                }
//                ps.executeUpdate();
//
//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//                return 0;
//            }
//        } catch (SQLException e) {
//            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
//        }
//    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


//    private void configureDatabase() throws ResponseException {
//        DatabaseManager.createDatabase();
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }
}
