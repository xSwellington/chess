package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;

    private final Board board;

    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVunerable;

    private final List<Piece> capturedPieces = new ArrayList<>();
    private final List<Piece> piecesOnTheBoard = new ArrayList<>();

    public ChessPiece getEnPassantVunerable() {
        return enPassantVunerable;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    public boolean isCheck() {
        return check;
    }

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public List<ChessPiece> getCapturedPieces() {
        return capturedPieces.stream().map(piece -> (ChessPiece) piece).toList();
    }

    private void nextTurn() {
        turn++;
        currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        return piecesOnTheBoard.stream()
                .filter(piece -> ((ChessPiece) piece).getColor() == color && piece instanceof King)
                .map(piece -> (ChessPiece) piece)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no " + color + " king on the board."));


    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        return piecesOnTheBoard.stream()
                .filter(piece -> ((ChessPiece) piece).getColor() == opponent(color))
                .anyMatch(piece -> {
                    boolean[][] mat = piece.possibleMoves();
                    return mat[kingPosition.getRow()][kingPosition.getCol()];
                });
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) return false;
        return piecesOnTheBoard.stream()
                .filter(piece -> ((ChessPiece) piece).getColor() == color)
                .map(piece -> (ChessPiece) piece)
                .anyMatch(piece -> {
                    boolean[][] mat = piece.possibleMoves();
                    for (int i = 0; i < board.getRows(); i++) {
                        for (int j = 0; j < board.getColumns(); j++) {
                            if ( mat[i][j] ) {
                                Position source = piece.getChessPosition().toPosition();
                                Position target = new Position(i, j);
                                Piece capturedPiece = makeMove(source, target);
                                boolean _testCheck = testCheck(color);
                                undoMove(source, target, capturedPiece);
                                if (!_testCheck) return false;
                            }
                        }
                    }
                    return true;
                });
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validadeSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validadeSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check.");
        }

        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        check = testCheck(opponent(currentPlayer));

        if (testCheckMate(opponent(currentPlayer)))
            checkMate = true;
        else
            nextTurn();

        if (movedPiece instanceof Pawn && target.getRow() == source.getRow() - 2 ||  (target.getRow() == source.getRow() + 2))
            enPassantVunerable = movedPiece;
        else
            enPassantVunerable = null;

        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);



        if (p instanceof King && target.getCol() == source.getCol() + 2) {
            Position sourceRook = new Position(source.getRow(), source.getCol()+3);
            Position targetRook = new Position(source.getRow(), source.getCol()+1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
            board.placePiece(rook, targetRook);
        }

        if (p instanceof King && target.getCol() == source.getCol() - 2) {
            Position sourceRook = new Position(source.getRow(), source.getCol()+4);
            Position targetRook = new Position(source.getRow(), source.getCol()+1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
            board.placePiece(rook, targetRook);
        }

        if (p instanceof Pawn) {
            if (source.getCol() != target.getCol() && capturedPiece == null){
                int row = target.getRow();
                row += p.getColor() == Color.WHITE ? 1 : - 1;
                Position pawnPosition = new Position(row, target.getCol());
                capturedPiece = board.removePiece(pawnPosition);
            }
        }

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.descreaseMoveCount();
        board.placePiece(p, source);

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        if (p instanceof King && target.getCol() == source.getCol() + 2) {
            Position sourceRook = new Position(source.getRow(), source.getCol()+3);
            Position targetRook = new Position(source.getRow(), source.getCol()+1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.descreaseMoveCount();
        }

        if (p instanceof King && target.getCol() == source.getCol() - 2) {
            Position sourceRook = new Position(source.getRow(), source.getCol()-4);
            Position targetRook = new Position(source.getRow(), source.getCol()-1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
            board.placePiece(rook, sourceRook);
            rook.descreaseMoveCount();
        }

        if (p instanceof Pawn) {
            if (source.getCol() != target.getCol() && capturedPiece == enPassantVunerable){
                ChessPiece pawn = (ChessPiece) board.removePiece(target);
                int row = p.getColor() == Color.WHITE ? 3 : 4;
                Position pawnPosition = new Position(row, target.getCol());
                board.placePiece(pawn, pawnPosition);
            }
        }
    }

    private void validadeSourcePosition(Position source) {
        if (!board.thereIsAPiece(source)) throw new ChessException("There is not piece on source position.");
        if (currentPlayer != ((ChessPiece) board.piece(source)).getColor())
            throw new ChessException("The chosen piece is not yours.");
        if (!board.piece(source).isThereAnyPossibleMove())
            throw new ChessException("There is no possible moves for the choose piece.");
    }


    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target))
            throw new ChessException("The chosen piece can't move to target position.");
    }

    public void initialSetup() {
        // Peões brancos
        for (char i = 'a'; i <= 'h'; i++) {
            placeNewPiece(i, 2, new Pawn(board, Color.WHITE, this));
        }

        // Peões pretos
        for (char i = 'a'; i <= 'h'; i++) {
            placeNewPiece(i, 7, new Pawn(board, Color.BLACK, this));
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
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
    }


}
