package lld.tictactoe;

/** Strategy seam: humans and bots both reduce to "give me a position". */
@FunctionalInterface
public interface MoveStrategy {
  Position choose(Board board);

  /** Simplest bot: first free cell in row-major order. */
  MoveStrategy FIRST_FREE = board -> board.freeCells().get(0);
}
