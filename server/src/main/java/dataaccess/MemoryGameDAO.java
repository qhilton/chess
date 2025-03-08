package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    Map<Integer, GameData> allGames;

    public MemoryGameDAO() {
        allGames = new HashMap<>();
    }

    @Override
    public void createGame(GameData gameData) {
        allGames.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (allGames.get(gameID) == null) {
            throw new DataAccessException("ID not found: " + gameID);
        }
        return allGames.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return allGames.values();
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {
        allGames.put(gameID, gameData);
    }

    @Override
    public void clear() {
        allGames.clear();
    }
}
