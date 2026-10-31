import lld.chess.GameStatus;

public class ChessSwitchBroken {
  static String label(GameStatus s) {
    return switch (s) {
      case GameStatus.InProgress p -> "play";
      case GameStatus.Checkmate c -> "mate";
    };
  }
}
