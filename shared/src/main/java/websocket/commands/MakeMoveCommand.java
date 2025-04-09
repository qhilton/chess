package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    ChessMove move;
    ChessGame.TeamColor playerColor;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessGame.TeamColor playerColor) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.playerColor = playerColor;

    }

    public ChessMove getMove() {
        return move;
    }

    public ChessGame.TeamColor getPlayerColor() {
//        if (playerColor == ChessGame.TeamColor.WHITE) {
//            return "white";
//        } else if (playerColor == ChessGame.TeamColor.BLACK) {
//            return "black";
//        }
//        return "";
        return playerColor;
    }
}