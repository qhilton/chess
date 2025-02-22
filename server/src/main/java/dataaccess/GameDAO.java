package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

public class GameDAO implements DataAccess {
    GameData gameData;

    //@Override
    //public void createUser(String username, String password, String email) {}

    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public void clear() {
        gameData = null;
    }

    //@Override
//    public UserData getUser() {
//        return null;
//    }

    //@Override
    public GameData getGame() {
        return gameData;
    }
}
