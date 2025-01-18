package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING:
                PieceMovesCalculator kingCalcuator = new KingMovesCalculator();
                return kingCalcuator.calculateKingMoves(board, myPosition);
        }
        return null;
    }

    @Override
    public String toString() {
        switch (type) {
            case KING:
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    return "k";
                } else {
                    return "K";
                }
            case QUEEN:
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    return "q";
                } else {
                    return "Q";
                }
            case ROOK:
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    return "r";
                } else {
                    return "R";
                }
            case BISHOP:
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    return "b";
                } else {
                    return "B";
                }
            case KNIGHT:
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    return "n";
                } else {
                    return "N";
                }
            case PAWN:
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    return "p";
                } else {
                    return "P";
                }
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
