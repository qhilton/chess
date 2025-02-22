package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.Map;

public class GameDAO implements DataAccess {
    Map<Integer, GameData> allGames;

    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        allGames.put(gameID, new GameData(gameID, whiteUsername, blackUsername, gameName, game));
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
