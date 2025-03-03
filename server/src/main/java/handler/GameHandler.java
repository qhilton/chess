package handler;

import service.GameService;

public class GameHandler {
    GameService gameService = new GameService();

    public void clear() {
        gameService.clear();
    }
}
