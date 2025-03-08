package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import service.GameService;

import java.util.Collection;

public class GameHandler {
    GameService gameService = new GameService();
    Gson serializer = new Gson();

    public String handleCreateGame(String authToken, String json, UserHandler userHandler) throws DataAccessException, ResponseException {
        var createGameRequest = serializer.fromJson(json, CreateGameRequest.class);
        CreateGameResult createGameResult = gameService.createGame(authToken, createGameRequest, userHandler.getUserService());
        return serializer.toJson(createGameResult);
    }

    public void handleJoinGame(String authToken, String json, UserHandler userHandler) throws DataAccessException {
        JoinGameRequest joinGameRequest = serializer.fromJson(json, JoinGameRequest.class);
        gameService.joinGame(authToken, joinGameRequest, userHandler.getUserService());
    }

    public String handleListGames(String json, UserHandler userHandler) throws DataAccessException {
        Collection<GameData> gameData = (gameService.listGames(serializer.fromJson(json, String.class), userHandler.getUserService()));
        ListGamesResult games = new ListGamesResult(gameData);
        return serializer.toJson(games);
    }


    public void clear() throws ResponseException, DataAccessException {
        gameService.clear();
    }
}
