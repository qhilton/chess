package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private static ChessGame game = new ChessGame();
    private static Integer[][] highlightBoard = new Integer[8][8];

    public static void drawChessBoard(ChessGame.TeamColor teamColor, ChessPosition position) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        setHighlightBoard(position);

        drawHeaders(out, teamColor);

        for (int boardRow = 8; boardRow > 0; boardRow--) {
            drawRow(out, boardRow, teamColor);
        }

        drawHeaders(out, teamColor);

        out.print(RESET_TEXT_COLOR);
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor teamColor) {
        String[] headers;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            headers = new String[] { "   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   " };
        } else {
            headers = new String[] { "   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   " };
        }
        for (int boardCol = 0; boardCol < 10; boardCol++) {
            drawHeader(out, headers[boardCol]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        setBorder(out);
        out.print(headerText);
        out.print(RESET_BG_COLOR);
    }

    private static void drawRow(PrintStream out, int row, ChessGame.TeamColor teamColor) {
        setBorder(out);

        if (teamColor == ChessGame.TeamColor.WHITE) {
            out.print(" " + (row) + " ");
            for (int col = 1; col < 9; col++) {
                drawCol(out, row, col);
            }
        } else {
            row = fixRow(row);
            out.print(" " + (row) + " ");
            for (int col = 8; col > 0; col--) {
                drawCol(out, row, col);
            }
        }

        setBorder(out);
        out.print(" " + (row) + " ");

        out.print(RESET_BG_COLOR);
        out.println();

    }

    private static void drawCol(PrintStream out, int row, int col) {
        if (row % 2 == 0) {
            if (col % 2 == 1) {
                setWhite(out);
                drawSquare(out, row, col);
            } else {
                setBlack(out);
                drawSquare(out, row, col);
            }
        } else {
            if (col % 2 == 1) {
                setBlack(out);
                drawSquare(out, row, col);
            } else {
                setWhite(out);
                drawSquare(out, row, col);
            }
        }
    }

    private static void drawSquare(PrintStream out, int row, int col) {

        setHighlightColor(out, row, col);
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        setColor(out, piece);
        drawPiece(out, piece);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
    }

    private static void setHighlightColor(PrintStream out, int row, int col) {
        if (highlightBoard[row-1][col-1] == 1) {
            setYellow(out);
        } else if (highlightBoard[row-1][col-1] == 2) {
            setGreen(out);
        }
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

    private static int fixRow(int row) {
        if (row == 8) {
            return  1;
        } else if (row == 7) {
            return 2;
        } else if (row == 6) {
            return 3;
        } else if (row == 5) {
            return 4;
        } else if (row == 4) {
            return 5;
        } else if (row == 3) {
            return 6;
        } else if (row == 2) {
            return 7;
        } else {
            return 8;
        }
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setHighlightBoard(ChessPosition position) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                highlightBoard[i][j] = 0;
            }
        }

        if (position.getRow() != 0) {
            highlightBoard[position.getRow()-1][position.getColumn()-1] = 1;
            if (game.getBoard().getPiece(position) != null) {
                ArrayList<ChessMove> potentialMoves = (ArrayList) game.validMoves(position);
                for (ChessMove move : potentialMoves) {
                    highlightBoard[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = 2;
                }
            }
        }
    }
}
