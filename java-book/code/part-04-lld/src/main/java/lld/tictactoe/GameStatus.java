package lld.tictactoe;

/** Closed set of outcomes (Java 17+ sealed). Switches over it need no default. */
public sealed interface GameStatus permits GameStatus.InProgress, GameStatus.Won, GameStatus.Draw {
  record InProgress(Mark next) implements GameStatus {}

  record Won(Mark winner) implements GameStatus {}

  record Draw() implements GameStatus {}
}
