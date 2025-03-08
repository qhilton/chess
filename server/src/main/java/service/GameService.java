package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;

import java.util.Collection;

public class GameService {
    MemoryGameDAO game = new MemoryGameDAO();
    private int nextID = 1;

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest, UserService userService) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Unauthorized create request");
        }

        try {
            userService.getAuthDAO().getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized create request");
        }

        int gameID = nextID++;
        game.createGame(new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame()));

        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest, UserService userService) throws DataAccessException {
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
            userService.getAuthDAO().getAuth(authToken);
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
        String username = userService.getAuthDAO().getAuth(authToken).username();

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

    public Collection<GameData> listGames(String authToken, UserService userService) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Unauthorized logout request");
        }

        try {
            userService.getAuthDAO().getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized logout request");
        }

        return game.listGames();

    }

    public void clear() {
        game.clear();
    }
}
