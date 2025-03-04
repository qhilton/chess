package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.LoginRequest;
import result.CreateGameResult;
import result.LoginResult;

import java.util.ArrayList;
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

    public ArrayList listGames(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Unauthorized logout request");
        }

        try {
            auth.getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized logout request");
        }

        return (ArrayList) game.listGames();

    }

    public void clear() {
        game.clear();
        auth.clear();
    }
}
