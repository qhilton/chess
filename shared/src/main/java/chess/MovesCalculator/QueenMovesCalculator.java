package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    private ArrayList<ChessMove> moves = new ArrayList<>();
    private boolean inBounds = true;

    @Override
    public Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = 1; inBounds; i++){ //top left
            if (myPosition.getRow() + i <= 8 && myPosition.getColumn() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()-i);
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //top right
            if (myPosition.getRow() + i <= 8 && myPosition.getColumn() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()+i);
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //bottom right
            if (myPosition.getRow() - i > 0 && myPosition.getColumn() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()+i);
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //bottom left
            if (myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()-i);
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //left
            if (myPosition.getColumn() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-i);
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //top
            if (myPosition.getRow() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn());
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //right
            if (myPosition.getColumn() + i <= 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+i);
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        inBounds = true;
        for (int i = 1; inBounds; i++){ //bottom
            if (myPosition.getRow() - i > 0) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn());
                addMove(board, myPosition, tempPosition);
            } else {
                inBounds = false;
            }
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {return moves;}

    private void addMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
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
}
