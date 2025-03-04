package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;

public class GameHandler {
    GameService gameService = new GameService();
    Gson serializer = new Gson();

    public String handleListGames(String json) throws DataAccessException {
        var listGamesResult = gameService.listGames(serializer.fromJson(json, String.class));
        return serializer.toJson(listGamesResult);
    }

    public String handleCreateGame(String authToken, String json, UserHandler userHandler) throws DataAccessException {
        var createGameRequest = serializer.fromJson(json, CreateGameRequest.class);
        CreateGameResult createGameResult = gameService.createGame(authToken, createGameRequest, userHandler.getUserService());
        return serializer.toJson(createGameResult);
    }


    public void clear() {
        gameService.clear();
    }
}
