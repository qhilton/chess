package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {

    ChessGame.TeamColor playerColor;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
    }

    public String getPlayerColor() {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            return "white";
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            return "black";
        }
        return "";
    }
}
