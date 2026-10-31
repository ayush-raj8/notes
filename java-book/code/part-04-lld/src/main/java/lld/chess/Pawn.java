package lld.chess;

import java.util.ArrayList;
import java.util.List;

/** Forward 1 (or 2 from home rank), diagonal capture. No en passant; promotion is handled by Game. */
public record Pawn(Color color) implements Piece {
  @Override
  public char letter() {
    return 'P';
  }

  @Override
  public List<Square> pseudoLegalTargets(Board board, Square from) {
    List<Square> out = new ArrayList<>();
    int dir = color == Color.WHITE ? 1 : -1;
    int home = color == Color.WHITE ? 1 : 6;
    int nr = from.rank() + dir;
    if (nr >= 0 && nr <= 7) {
      Square one = new Square(from.file(), nr);
      if (board.at(one) == null) {
        out.add(one);
        if (from.rank() == home) {
          Square two = new Square(from.file(), from.rank() + 2 * dir);
          if (board.at(two) == null) {
            out.add(two);
          }
        }
      }
      for (Square cap : captureSquares(from, dir)) {
        Piece at = board.at(cap);
        if (at != null && at.color() != color) {
          out.add(cap);
        }
      }
    }
    return out;
  }

  @Override
  public List<Square> attacks(Board board, Square from) {
    int dir = color == Color.WHITE ? 1 : -1;
    return captureSquares(from, dir);
  }

  private static List<Square> captureSquares(Square from, int dir) {
    List<Square> out = new ArrayList<>();
    int nr = from.rank() + dir;
    if (nr < 0 || nr > 7) {
      return out;
    }
    for (int df : new int[] {-1, 1}) {
      int cf = from.file() + df;
      if (cf >= 0 && cf <= 7) {
        out.add(new Square(cf, nr));
      }
    }
    return out;
  }
}
