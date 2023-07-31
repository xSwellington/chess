package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.*;

public class ChessMatch {

    private Board board;

    public ChessMatch() {
        board = new Board(8,8);
        initialSetup();
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for(int i=0; i<board.getRows(); i++){
            for (int j = 0; j<board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public void initialSetup(){
        // Peões brancos
        for (int i = 0; i<8; i++) {
            board.placePiece(new Pawn(board, Color.WHITE), new Position(1, i));
        }

        // Peões pretos
        for (int i = 0; i<8; i++) {
            board.placePiece(new Pawn(board, Color.BLACK), new Position(6, i));
        }

        // Torres
        board.placePiece(new Rook(board, Color.WHITE), new Position(0, 0));
        board.placePiece(new Rook(board, Color.WHITE), new Position(0, 7));
        board.placePiece(new Rook(board, Color.BLACK), new Position(7, 0));
        board.placePiece(new Rook(board, Color.BLACK), new Position(7, 7));

        // Cavalos
        board.placePiece(new Knight(board, Color.WHITE), new Position(0, 1));
        board.placePiece(new Knight(board, Color.WHITE), new Position(0, 6));
        board.placePiece(new Knight(board, Color.BLACK), new Position(7, 1));
        board.placePiece(new Knight(board, Color.BLACK), new Position(7, 6));

        // Bispos
        board.placePiece(new Bishop(board, Color.WHITE), new Position(0, 2));
        board.placePiece(new Bishop(board, Color.WHITE), new Position(0, 5));
        board.placePiece(new Bishop(board, Color.BLACK), new Position(7, 2));
        board.placePiece(new Bishop(board, Color.BLACK), new Position(7, 5));

        // Rainhas
        board.placePiece(new Queen(board, Color.WHITE), new Position(0, 3));
        board.placePiece(new Queen(board, Color.BLACK), new Position(7, 3));

        // Reis
        board.placePiece(new King(board, Color.WHITE), new Position(0, 4));
        board.placePiece(new King(board, Color.BLACK), new Position(7, 4));
    }
}