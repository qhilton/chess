package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public class GameService {
    GameDAO game = new GameDAO();
    AuthDAO auth = new AuthDAO();

    public void clear() {
        game.clear();
        auth.clear();
    }
}
