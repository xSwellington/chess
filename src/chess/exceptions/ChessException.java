package chess.exceptions;

import boardgame.exceptions.BoardException;

public class ChessException extends BoardException {
    public ChessException(String message) {
        super(message);
    }

}
