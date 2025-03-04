package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.GamesList;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import result.CreateGameResult;
import result.LoginResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class GameService {
    GameDAO game = new GameDAO();
    AuthDAO auth = new AuthDAO();

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

        int gameID = game.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest, UserService userService) throws DataAccessException {
        GameData currentGame;
        if (authToken == null) {
            throw new DataAccessException("Unauthorized create request");
        }
        else if (joinGameRequest.gameID() <= 0 || joinGameRequest.playerColor() == null || (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK"))) {
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
        else if (joinGameRequest.playerColor().equals("BLACK")) {
            if (blackUserName != null && !currentGame.blackUsername().equals(username)) {
                throw new DataAccessException("Color already taken");
            }
            blackUserName = username;
        }

        GameData newGame = new GameData(joinGameRequest.gameID(), whiteUserName, blackUserName, currentGame.gameName(), currentGame.game());
        game.updateGame(joinGameRequest.gameID(), newGame);

//        try {
//            GameData newGame = new GameData(joinGameRequest.gameID(), whiteUserName, blackUserName, currentGame.gameName(), currentGame.game());
//            game.updateGame(joinGameRequest.gameID(), newGame);
//        }
//        catch (DataAccessException e) {
//            throw new DataAccessException("Unauthorized create request");
//        }
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

//        Collection<GamesList> list = new ArrayList<>();
//        //Collection<GameData> x = game.listGames();
//        for (GameData data : game.listGames()) {
//            list.add(new GamesList(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName()));
//        }
        return game.listGames();

    }

    public void clear() {
        game.clear();
        auth.clear();
    }
}
