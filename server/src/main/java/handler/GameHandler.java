package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import execption.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import service.GameService;

import java.util.Collection;

public class GameHandler {
    public GameService gameService = new GameService();
    Gson serializer = new Gson();

    public String handleCreateGame(String authToken, String json) throws DataAccessException, ResponseException {
        var createGameRequest = serializer.fromJson(json, CreateGameRequest.class);
        CreateGameResult createGameResult = gameService.createGame(authToken, createGameRequest);
        return serializer.toJson(createGameResult);
    }

    public void handleJoinGame(String authToken, String json) throws DataAccessException, ResponseException {
        JoinGameRequest joinGameRequest = serializer.fromJson(json, JoinGameRequest.class);
        gameService.joinGame(authToken, joinGameRequest);
    }

    public String handleListGames(String json) throws DataAccessException, ResponseException {
        Collection<GameData> gameData = (gameService.listGames(serializer.fromJson(json, String.class)));
        ListGamesResult games = new ListGamesResult(gameData);
        return serializer.toJson(games);
    }


    public void clear() throws ResponseException, DataAccessException {
        gameService.clear();
    }
}
