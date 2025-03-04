package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameDAO implements DataAccess {
    Map<Integer, GameData> allGames;
    private int nextID = 1;

    public GameDAO() {
        allGames = new HashMap<>();
    }

    public int createGame(String gameName) {
        int gameID = nextID++;
        allGames.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (allGames.get(gameID) == null) {
            throw new DataAccessException("ID not found: " + gameID);
        }
        return allGames.get(gameID);
    }

    public Collection<GameData> listGames() {
        return allGames.values();
    }

    public void updateGame(int gameID, GameData gameData) {
        //GameData gameData = allGames.get(gameID);
        allGames.put(gameID, gameData);
    }

    @Override
    public void clear() {
        allGames.clear();
    }
}
