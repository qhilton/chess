package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData gameData);
    void clear();
}
