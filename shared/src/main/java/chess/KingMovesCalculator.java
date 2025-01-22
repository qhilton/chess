package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    @Override
    public Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
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

    @Override
    public Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }

    private void addMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
        if ((board.getPiece(tempPosition) == null) || (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())) {
            moves.add(new ChessMove(myPosition, tempPosition, null));
        }
    }
}
