package lld.chess;

public sealed interface GameStatus
    permits GameStatus.InProgress, GameStatus.Checkmate, GameStatus.Stalemate {
  record InProgress(Color turn, boolean check) implements GameStatus {}

  record Checkmate(Color winner) implements GameStatus {}

  record Stalemate() implements GameStatus {}
}
