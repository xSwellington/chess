package boardgame;

public abstract class Piece {
    protected Position position;
    private final Board board;

    public Piece(Board board) {
        this.board = board;
    }
    protected Board getBoard() {
        return board;
    }

    public abstract boolean[][] possibleMoves();
    public boolean possibleMove(Position position){
        return possibleMoves()[position.getRow()][position.getCol()];
    }

    public boolean isThereAnyPossibleMove(){
        boolean[][] mat = possibleMoves();
        for (boolean[] booleans : mat) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) return true;
            }
        }
        return false;
    }
}
