package lld.chess;

import java.util.List;

public record Rook(Color color) implements Piece {
  private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

  @Override
  public char letter() {
    return 'R';
  }

  @Override
  public List<Square> pseudoLegalTargets(Board board, Square from) {
    return Piece.slide(board, from, color, DIRS);
  }
}
