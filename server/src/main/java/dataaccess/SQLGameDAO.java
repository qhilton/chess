package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import execption.ResponseException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SQLGameDAO implements GameDAO {
    private Gson serializer = new Gson();


    public SQLGameDAO() throws ResponseException, DataAccessException {
        DAOUtils.initDAO(createStatements);
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                preparedStatement.setString(5, serializeGame(gameData.game()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Game already exists with the ID: " + gameData.gameID());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var result = preparedStatement.executeQuery()) {
                    if (result != null && result.next()) {
                        var whiteUsername = result.getString("whiteUsername");
                        var blackUsername = result.getString("blackUsername");
                        var gameName = result.getString("gameName");
                        var game = deserializeGame(result.getString("game"));
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ID not found: " + gameID);
        }
        throw new DataAccessException("ID not found: " + gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        Map<Integer, GameData> allGames = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        var gameID = result.getInt("gameID");
                        allGames.put(gameID, getGame(gameID));
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            return null;
        }

        return allGames.values();
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {

                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, serializeGame(gameData.game()));
                preparedStatement.setInt(5, gameID);
                if (preparedStatement.executeUpdate() == 0) {
                    throw new DataAccessException("ID not found: " + gameID);
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            //throw new DataAccessException("" + e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              gameID INT NOT NULL,
              whiteUsername varchar(256),
              blackUsername varchar(256),
              gameName varchar(256),
              game TEXT,
              PRIMARY KEY (gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private String serializeGame(ChessGame game) {
        return serializer.toJson(game);
    }

    private ChessGame deserializeGame(String json) {
        return serializer.fromJson(json, ChessGame.class);
    }
}
