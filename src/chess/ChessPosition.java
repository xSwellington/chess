package chess;

import boardgame.Position;
import chess.exceptions.ChessException;

public class ChessPosition {
    private final char column;
    private final int row;

    public ChessPosition(char column, int row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 8)
            throw new ChessException("Error instantiating ChessPosition. Valid values for column is a to h and row is 1 to 8.");
        this.column = column;
        this.row = row;
    }


    protected Position toPosition(){
        return new Position(8 - row, column - 'a');
    }

    protected static ChessPosition fromPosition(Position position) {
       return new ChessPosition((char)('a' + position.getCol()),8 - position.getRow());
    }

    @Override
    public String toString() {
        return String.format("%s%d", column, row);
    }
}
