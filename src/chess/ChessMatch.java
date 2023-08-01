package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
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

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validadeSourcePosition(source);
        return (ChessPiece) makeMove(source, target);
    }

    private Piece makeMove(Position source, Position target) {
        Piece p = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        return capturedPiece;

    }

    private void validadeSourcePosition(Position source) {
        if (!board.thereIsAPiece(source)) throw new ChessException("There is not piece on source position.");
    }

    public void initialSetup(){
        // Peões brancos
        for (char i = 'a'; i<='h'; i++) {
            placeNewPiece(i, 2, new Pawn(board, Color.WHITE));
        }

        // Peões pretos
        for (char i = 'a'; i<='h'; i++) {
            placeNewPiece(i, 7, new Pawn(board, Color.BLACK));
        }

        // Torres
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));

//        // Cavalos
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));


        // Bispos
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));

        // Rainhas
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));

        // Reis
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
    }


}
