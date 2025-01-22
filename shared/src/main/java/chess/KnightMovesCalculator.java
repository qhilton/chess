package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    @Override
    public Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
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

    @Override
    public Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }

    private void addMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
        if ((board.getPiece(tempPosition) == null) || (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())) {
            moves.add(new ChessMove(myPosition, tempPosition, null));
        }
    }
}
