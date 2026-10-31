package lld.chess;

import java.util.List;

public record King(Color color) implements Piece {
  private static final int[][] DELTAS = {
    {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
  };

  @Override
  public char letter() {
    return 'K';
  }

  @Override
  public List<Square> pseudoLegalTargets(Board board, Square from) {
    return Piece.leaps(board, from, color, DELTAS);
  }
}
