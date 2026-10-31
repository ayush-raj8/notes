package lld.chess;

import java.util.List;

public record Knight(Color color) implements Piece {
  private static final int[][] DELTAS = {
    {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
  };

  @Override
  public char letter() {
    return 'N';
  }

  @Override
  public List<Square> pseudoLegalTargets(Board board, Square from) {
    return Piece.leaps(board, from, color, DELTAS);
  }
}
