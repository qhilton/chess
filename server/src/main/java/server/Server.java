package server;

import handler.UserHandler;
import spark.*;

public class Server {
    UserHandler userHandler = new UserHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);

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
//        String registerUser = userHandler.handleRegister(req.body());
//        res.status(200);
//        return userHandler.handleRegister(req.body());

        try {
            String registerUser = userHandler.handleRegister(req.body());
            res.status(200);
            return registerUser;
        } catch (Exception e) {
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }
}
