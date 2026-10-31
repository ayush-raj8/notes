package lld.chess;

public final class StatusText {
  private StatusText() {}

  public static String of(GameStatus status) {
    return switch (status) {
      case GameStatus.InProgress p ->
          p.turn() + (p.check() ? " to move (check)" : " to move");
      case GameStatus.Checkmate c -> "checkmate " + c.winner() + " wins";
      case GameStatus.Stalemate s -> "stalemate";
    };
  }
}
