import lld.tictactoe.GameStatus;

public class StatusSwitchBroken {
  static String text(GameStatus s) {
    return switch (s) {
      case GameStatus.InProgress ip -> "next";
      case GameStatus.Won w -> "won";
    };
  }
}
