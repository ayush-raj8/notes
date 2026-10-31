package lld.chess;

import java.util.List;

public record Bishop(Color color) implements Piece {
  private static final int[][] DIRS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

  @Override
  public char letter() {
    return 'B';
  }

  @Override
  public List<Square> pseudoLegalTargets(Board board, Square from) {
    return Piece.slide(board, from, color, DIRS);
  }
}
