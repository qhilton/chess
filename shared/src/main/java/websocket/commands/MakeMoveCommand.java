package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    String authToken;
    int gameID;
    CommandType commandType;
    ChessMove move;
    ChessGame.TeamColor color;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        //this.commandType = CommandType.MAKE_MOVE;
        this.move = move;
    }

    @Override
    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}