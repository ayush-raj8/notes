package lld.tictactoe;

public final class StatusText {
  private StatusText() {}

  /** Exhaustive pattern switch over the sealed GameStatus (Java 21 record patterns). */
  public static String of(GameStatus status) {
    return switch (status) {
      case GameStatus.InProgress(Mark next) -> "next " + next;
      case GameStatus.Won(Mark winner) -> "winner " + winner;
      case GameStatus.Draw d -> "draw";
    };
  }
}
