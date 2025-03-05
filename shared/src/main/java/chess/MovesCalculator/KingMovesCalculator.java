package chess;

import chess.MovesCalculator.MovesCalculatorUtils;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    @Override
    public Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        return MovesCalculatorUtils.calculateKingLikeMoves(board, myPosition);
    }

    @Override
    public Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }

    @Override
    public Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }

    @Override
    public Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }

    @Override
    public Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }

    @Override
    public Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        return moves;
    }
}
