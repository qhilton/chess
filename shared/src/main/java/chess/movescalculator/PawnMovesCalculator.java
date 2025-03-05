package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private ArrayList<ChessMove> moves = new ArrayList<>();

    public Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, null);
            }
            if (myPosition.getRow()+1 < 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, null);
                capture(board, myPosition, tempPosition, null);
            }
            else if (myPosition.getRow()+1 == 8) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
            }
        }

        else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, null);
            }
            if (myPosition.getRow()-1 > 1) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, null);
                capture(board, myPosition, tempPosition, null);
            }
            else if (myPosition.getRow()-1 == 1) {
                ChessPosition tempPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                addMove(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.QUEEN);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.ROOK);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.BISHOP);
                capture(board, myPosition, tempPosition, ChessPiece.PieceType.KNIGHT);
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

    public void addMove(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition, ChessPiece.PieceType promo) {
        if(board.getPiece(tempPosition) == null) {
            moves.add(new ChessMove(myPosition, tempPosition, promo));
        }
    }

    public void capture(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition, ChessPiece.PieceType promo) {
        if(tempPosition.getColumn()-1 > 0){
            ChessPosition temp = new ChessPosition(tempPosition.getRow(), tempPosition.getColumn()-1);
            if(board.getPiece(temp) != null && board.getPiece(temp).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, temp, promo));
            }
        }
        if(tempPosition.getColumn()+1 <= 8){
            ChessPosition temp = new ChessPosition(tempPosition.getRow(), tempPosition.getColumn()+1);
            if(board.getPiece(temp) != null && board.getPiece(temp).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, temp, promo));
            }
        }
    }

}
