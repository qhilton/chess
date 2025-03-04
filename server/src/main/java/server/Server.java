package server;

import handler.GameHandler;
import handler.UserHandler;
import spark.*;

public class Server {
    UserHandler userHandler = new UserHandler();
    GameHandler gameHandler = new GameHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) {
        try {
            var registerUser = userHandler.handleRegister(req.body());
            res.status(200);
            return registerUser;
        } catch (Exception e) {
            if (e.getMessage().contains("Bad request")) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
            else if (e.getMessage().contains("User already exists")) {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }

            res.status(500);
            return "{ \"message\": \"Error:\" }";
        }
    }

    private Object login(Request req, Response res) {
        try {
            var loginUser = userHandler.handleLogin(req.body());
            res.status(200);
            return loginUser;
        } catch (Exception e) {
            if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            res.status(500);
            return "{ \"message\": \"Error:\" }";
        }
    }

    private Object logout(Request req, Response res) {
        try {
            userHandler.handleLogout(req.headers("authorization"));
            res.status(200);
            return "{}";
        } catch (Exception e) {
            if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            res.status(500);
            return "{ \"message\": \"Error:\" }";
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            var createGame = gameHandler.handleCreateGame(authToken, req.body(), userHandler);
            res.status(200);
            return createGame;
        } catch (Exception e) {
            if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            res.status(500);
            return "{ \"message\": \"Error:\" }";
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            gameHandler.handleJoinGame(authToken, req.body(), userHandler);
            res.status(200);
            return "{}";
        } catch (Exception e) {
            if (e.getMessage().contains("Bad request")) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
            else if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }
            else if (e.getMessage().contains("Color already taken")) {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }

            res.status(500);
            return "{ \"message\": \"Error:\" }";
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            var games = gameHandler.handleListGames(req.headers("authorization"), userHandler);
            res.status(200);
            return games;
        } catch (Exception e) {
            if (e.getMessage().contains("Unauthorized")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            res.status(500);
            return "{ \"message\": \"Error:\" }";
        }
    }

    private Object clear(Request req, Response res) {try {
            userHandler.clear();
            gameHandler.clear();
            res.status(200);
            return "";
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: (description of error)\" }";
        }
    }
}
