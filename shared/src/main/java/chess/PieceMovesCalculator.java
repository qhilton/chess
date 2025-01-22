package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition);
    Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition);
    Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition);
    Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition);
    Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition);
    Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition);
}
