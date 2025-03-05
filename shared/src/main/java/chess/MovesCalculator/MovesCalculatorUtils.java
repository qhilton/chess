package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class MovesCalculatorUtils {
    private static ArrayList<ChessMove> moves = new ArrayList<>();
    private static boolean inBounds = true;

    public static Collection<ChessMove> calculateRookLikeMoves(ChessBoard board, ChessPosition myPosition) {
        moves = new ArrayList<>();
        inBounds = true;

        for (int i = 1; inBounds; i++){ //left
            if (myPosition.getColumn() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-i);
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //top
            if (myPosition.getRow() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn());
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //right
            if (myPosition.getColumn() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+i);
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //bottom
            if (myPosition.getRow() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn());
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        return moves;
    }

    public static Collection<ChessMove> calculateBishopLikeMoves(ChessBoard board, ChessPosition myPosition) {
        moves = new ArrayList<>();
        inBounds = true;

        for (int i = 1; inBounds; i++){ //top left
            if (myPosition.getRow() + i <= 8 && myPosition.getColumn() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()-i);
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //top right
            if (myPosition.getRow() + i <= 8 && myPosition.getColumn() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()+i);
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //bottom right
            if (myPosition.getRow() - i > 0 && myPosition.getColumn() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()+i);
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //bottom left
            if (myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()-i);
                addQueenLikeMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        return moves;
    }

    public static Collection<ChessMove> calculateKnightLikeMoves(ChessBoard board, ChessPosition myPosition) {
        moves = new ArrayList<>();
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 2 > 0) { // left up
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() - 1 > 0) { // up left
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() + 1 <= 8) { // up right
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 2 <= 8) { // right up
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() + 2 <= 8) { // right down
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() - 2 > 0 && myPosition.getColumn() + 1 <= 8) { // down right
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() - 2 > 0 && myPosition.getColumn() - 1 > 0) { // down left
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() - 2 > 0) { // left down
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
            addMove(board, myPosition, tempPosition);
        }
        return moves;
    }

    public static Collection<ChessMove> calculateKingLikeMoves(ChessBoard board, ChessPosition myPosition) {
        moves = new ArrayList<>();
        if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() - 1 > 0) { // bottom left
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getColumn() - 1 > 0) { // left
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 1 > 0) { // top left
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() + 1 <= 8) { // top
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 1 <= 8) { // top right
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getColumn() + 1 <= 8) { // right
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() + 1 <= 8) { // bottom right
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            addMove(board, myPosition, tempPosition);
        }
        if (myPosition.getRow() - 1 > 0) { // bottom
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            addMove(board, myPosition, tempPosition);
        }
        return moves;
    }

    private static void addQueenLikeMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
        if (board.getPiece(tempPosition) == null) {
            moves.add(new ChessMove(myPosition, tempPosition, null));
        }
        else if (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            moves.add(new ChessMove(myPosition, tempPosition, null));
            inBounds = false;
        }
        else if (board.getPiece(tempPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
            inBounds = false;
        }
    }

    public static void addMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
        if ((board.getPiece(tempPosition) == null) || (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())) {
            moves.add(new ChessMove(myPosition, tempPosition, null));
        }
    }
}
