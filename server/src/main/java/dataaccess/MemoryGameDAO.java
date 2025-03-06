package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    Map<Integer, GameData> allGames;
    private int nextID = 1;

    public MemoryGameDAO() {
        allGames = new HashMap<>();
    }

    @Override
    public int createGame(String gameName) {
        int gameID = nextID++;
        allGames.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
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
