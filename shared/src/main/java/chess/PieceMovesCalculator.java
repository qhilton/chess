package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition);
}
