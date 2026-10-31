package lld.tictactoe;

/** Unchecked: a rejected move leaves the game state unchanged. */
public class InvalidMoveException extends RuntimeException {
  public InvalidMoveException(String message) {
    super(message);
  }
}
