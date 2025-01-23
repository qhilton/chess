package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private ArrayList<ChessMove> moves = new ArrayList<>();
    private boolean inBounds = true;

    @Override
    public Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) { //start position
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, null);
            }
            if (myPosition.getRow() + 1 <= 8) { //advance
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                if (tempPosition.getRow() != 8) { //normal move
                    addMove(board, myPosition, tempPosition, null);
                } else { //promotion
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                }
                if (myPosition.getColumn() + 1 <= 8) { //capture right
                    tempPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                    if (tempPosition.getRow() != 8) { //normal capture
                        captureMove(board, myPosition, tempPosition, null);
                    } else { //promotion
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                    }
                }
                if (myPosition.getColumn() - 1 > 0) { //capture left
                    tempPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                    if (tempPosition.getRow() != 8) { //normal capture
                        captureMove(board, myPosition, tempPosition, null);
                    } else { //promotion
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                    }
                }
            }
        }
        else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null) { //start position
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, null);
            }
            if (myPosition.getRow() - 1 > 0) { //advance
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                if (tempPosition.getRow() != 1) { //normal move
                    addMove(board, myPosition, tempPosition, null);
                } else { //promotion
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                    addMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                }
                if (myPosition.getColumn() + 1 <= 8) { //capture right
                    tempPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                    if (tempPosition.getRow() > 1) { //normal capture
                        captureMove(board, myPosition, tempPosition, null);
                    } else { //promotion
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                    }
                }
                if (myPosition.getColumn() - 1 > 0) { //capture left
                    tempPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                    if (tempPosition.getRow() > 1) { //normal capture
                        captureMove(board, myPosition, tempPosition, null);
                    } else { //promotion
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                        captureMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition) {return moves;}
    @Override
    public Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {return moves;}

    private void addMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition, ChessPiece.PieceType promo) {
        if (board.getPiece(tempPosition) == null) {
            moves.add(new ChessMove(myPosition, tempPosition, promo));
        }
//        else if (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
//            moves.add(new ChessMove(myPosition, tempPosition, null));
//            inBounds = false;
//        }
//        else if (board.getPiece(tempPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
//            inBounds = false;
//        }
    }

    private void captureMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition, ChessPiece.PieceType promo) {
        if (board.getPiece(tempPosition) != null) {
            if (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, tempPosition, promo));
                //inBounds = false;
            }
        }
//        else if (board.getPiece(tempPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
//            inBounds = false;
//        }
    }

}
