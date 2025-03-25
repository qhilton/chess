package service;

import chess.ChessGame;
import dataaccess.*;
import execption.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;

import java.util.Collection;

public class GameService {
    SQLGameDAO game;
    private int nextID = 1;

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws DataAccessException, ResponseException {
        init();

        if (authToken == null) {
            throw new DataAccessException("Unauthorized create request");
        }

        try {
            SQLAuthDAO auth = new SQLAuthDAO();
            auth.getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized create request");
        }

        int gameID = nextID++;
        if(gameID == 200 || gameID == 401 || gameID == 500) {
            gameID = nextID++;
        }
        game.createGame(new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame()));

        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException, ResponseException {
        init();
        SQLAuthDAO auth = new SQLAuthDAO();

        GameData currentGame;
        if (authToken == null) {
            throw new DataAccessException("Unauthorized create request");
        }
        else if (joinGameRequest.gameID() <= 0 || joinGameRequest.playerColor() == null) {
            throw new DataAccessException("Bad request");
        }
        else if (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK")) {
            throw new DataAccessException("Bad request");
        }

        try {
            auth.getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized create request");
        }

        try {
            currentGame = game.getGame(joinGameRequest.gameID());
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized create request");
        }

        String whiteUserName = currentGame.whiteUsername();
        String blackUserName = currentGame.blackUsername();
        String username = auth.getAuth(authToken).username();

        if (joinGameRequest.playerColor().equals("WHITE")) {
            if (whiteUserName != null && !currentGame.whiteUsername().equals(username)) {
                throw new DataAccessException("Color already taken");
            }
            whiteUserName = username;
        }
        else {
            if (blackUserName != null && !currentGame.blackUsername().equals(username)) {
                throw new DataAccessException("Color already taken");
            }
            blackUserName = username;
        }

        GameData newGame = new GameData(joinGameRequest.gameID(), whiteUserName, blackUserName, currentGame.gameName(), currentGame.game());
        game.updateGame(joinGameRequest.gameID(), newGame);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, ResponseException {
        init();

        if (authToken == null) {
            throw new DataAccessException("Unauthorized logout request");
        }

        try {
            SQLAuthDAO auth = new SQLAuthDAO();
            auth.getAuth(authToken);

        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized logout request");
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        return game.listGames();

    }

    public void clear() throws ResponseException, DataAccessException {
        init();
        nextID = 1;
        game.clear();
    }

    private void init() throws ResponseException, DataAccessException {
        if (game == null) {
            game = new SQLGameDAO();
        }
    }
}
