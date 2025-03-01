package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDAO implements DataAccess {
    Map<Integer, GameData> allGames;

    public GameDAO() {
        allGames = new HashMap<>();
    }

    public void createGame(GameData gameData) {
        allGames.put(gameData.gameID(), gameData);
    }

    public GameData getGame(int gameID) {
        return allGames.get(gameID);
    }

    public Collection<GameData> listGames() {
        return allGames.values();
    }

    public void updateGame(int gameID, ChessGame game) {
        GameData gameData = allGames.get(gameID);
        allGames.put(gameID, new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
    }

    @Override
    public void clear() {
        allGames = null;
    }
}
