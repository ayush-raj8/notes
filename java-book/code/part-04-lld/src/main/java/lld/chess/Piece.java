package lld.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Sealed piece hierarchy. {@link #pseudoLegalTargets} ignores check; {@link Game} filters moves that
 * leave the mover's king in check.
 */
public sealed interface Piece permits King, Queen, Rook, Bishop, Knight, Pawn {
  Color color();

  char letter();

  List<Square> pseudoLegalTargets(Board board, Square from);

  /** Squares this piece attacks (for check). Default = pseudo-legal; pawns override. */
  default List<Square> attacks(Board board, Square from) {
    return pseudoLegalTargets(board, from);
  }

  default String symbol() {
    char c = letter();
    return color() == Color.WHITE ? String.valueOf(c) : String.valueOf(Character.toLowerCase(c));
  }

  static List<Square> slide(Board board, Square from, Color color, int[][] dirs) {
    List<Square> out = new ArrayList<>();
    for (int[] d : dirs) {
      int f = from.file() + d[0];
      int r = from.rank() + d[1];
      while (f >= 0 && f <= 7 && r >= 0 && r <= 7) {
        Square to = new Square(f, r);
        Piece at = board.at(to);
        if (at == null) {
          out.add(to);
        } else {
          if (at.color() != color) {
            out.add(to);
          }
          break;
        }
        f += d[0];
        r += d[1];
      }
    }
    return out;
  }

  static List<Square> leaps(Board board, Square from, Color color, int[][] deltas) {
    List<Square> out = new ArrayList<>();
    for (int[] d : deltas) {
      int f = from.file() + d[0];
      int r = from.rank() + d[1];
      if (f < 0 || f > 7 || r < 0 || r > 7) {
        continue;
      }
      Square to = new Square(f, r);
      Piece at = board.at(to);
      if (at == null || at.color() != color) {
        out.add(to);
      }
    }
    return out;
  }
}
