package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {
    private int turnTracker;
    private ChessBoard board;
    private boolean liveGame;

    public ChessGame() {
        turnTracker = 0;
        board = new ChessBoard();
        board.resetBoard();
        liveGame = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (turnTracker % 2 == 0) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (getTeamTurn() != team) {
            turnTracker++;
        }
    }

    public void disableGame() {
        liveGame = false;
    }

    public boolean returnLiveGame() {
       return liveGame;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = new ChessPiece(board.getPiece(startPosition).getTeamColor(), board.getPiece(startPosition).getPieceType());
        ArrayList<ChessMove> potentialMoves = (ArrayList<ChessMove>) currentPiece.pieceMoves(board, startPosition);
        ChessGame game = new ChessGame();

        for (ChessMove move : potentialMoves) {
            game.setBoard(board);
            game = game.clone();
            ChessBoard tempBoard = game.getBoard();
            tempBoard.addPiece(move.getEndPosition(), currentPiece);
            tempBoard.addPiece(move.getStartPosition(), null);

            if (!game.isInCheck(currentPiece.getTeamColor())) {
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        ArrayList<ChessMove> currentMoves = (ArrayList<ChessMove>) validMoves(move.getStartPosition());
        if (currentPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        else if (currentMoves.contains(move)) {
            if(currentPiece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.addPiece(move.getEndPosition(), currentPiece);
            }
            board.addPiece(move.getStartPosition(), null);
            turnTracker++;
        } else {
            throw new InvalidMoveException("Invalid move: " + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition myPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(myPosition);

                if (canCheck(currentPiece, kingPosition, myPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canCheck(ChessPiece currentPiece, ChessPosition kingPosition, ChessPosition myPosition) {
        if (currentPiece != null) {
            ArrayList<ChessMove> currentMoves = (ArrayList<ChessMove>) currentPiece.pieceMoves(board, myPosition);
            for (ChessMove move : currentMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return noValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return noValidMove(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition findKing(TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition myPosition = new ChessPosition(i, j);
                if (board.getPiece(myPosition) != null) {
                    boolean sameColor = board.getPiece(myPosition).getTeamColor() == color;
                    if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KING && sameColor) {
                        return myPosition;
                    }
                }
            }
        }
        return null;
    }

    public boolean noValidMove(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition myPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(myPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    ArrayList<ChessMove> currentMoves = (ArrayList<ChessMove>) validMoves(myPosition);
                    if (!currentMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ChessGame clone() {
        try {
            ChessGame myClone = (ChessGame) super.clone();
            //int clonedTurnTracker = (int) getTeamTurn().clone();
            ChessBoard clonedChessBoard = (ChessBoard) getBoard().clone();
            myClone.setBoard(clonedChessBoard);
            return myClone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turnTracker == chessGame.turnTracker && Objects.deepEquals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnTracker, board);
    }
}
