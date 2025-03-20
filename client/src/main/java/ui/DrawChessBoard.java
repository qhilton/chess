package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private static ChessGame game = new ChessGame();

    public static void main(String[] args) {
        drawChessBoard();
    }

    private static void drawChessBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        for (int boardRow = 8; boardRow > 0; boardRow--) {
            drawRow(out, boardRow);
        }

        drawHeaders(out);

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {
        String[] headers = { "   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   " };
        for (int boardCol = 0; boardCol < 10; boardCol++) {
            drawHeader(out, headers[boardCol]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        printHeaderText(out, headerText);
        out.print(RESET_BG_COLOR);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(player);
    }

    private static void drawRow(PrintStream out, int row) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + (row) + " ");

        for (int col = 1; col < 9; col++) {
            if (row % 2 == 0) {
                if (col % 2 == 1) {
                    drawWhiteSquare(out, row, col);
                } else {
                    drawBlackSquare(out, row, col);
                }
            }
            else {
                if (col % 2 == 1) {
                    drawBlackSquare(out, row, col);
                } else {
                    drawWhiteSquare(out, row, col);
                }
            }
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + (row) + " ");

        out.print(RESET_BG_COLOR);
        out.println();

    }

    private static void drawWhiteSquare(PrintStream out, int row, int col) {
        out.print(SET_BG_COLOR_WHITE);
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        setColor(out, piece);
        drawPiece(out, piece);
    }

    private static void drawBlackSquare(PrintStream out, int row, int col) {
        out.print(SET_BG_COLOR_BLACK);
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        setColor(out, piece);
        drawPiece(out, piece);
    }

    private static void setColor(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                out.print(SET_TEXT_COLOR_BLUE);
            } else {
                out.print(SET_TEXT_COLOR_RED);
            }
        }
    }

    private static void drawPiece(PrintStream out, ChessPiece piece) {
        if (piece == null) {
            out.print("   ");
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            out.print(" N ");
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            out.print(" K ");
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            out.print(" R ");
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            out.print(" B ");
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            out.print(" Q ");
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            out.print(" P ");
        }
    }
}
