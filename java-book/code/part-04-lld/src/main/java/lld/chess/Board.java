package lld.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 8×8 grid. Indexing: {@code cells[file][rank]}. */
public final class Board {
  private final Piece[][] cells = new Piece[8][8];

  public Board() {}

  public Board(Board other) {
    for (int f = 0; f < 8; f++) {
      System.arraycopy(other.cells[f], 0, cells[f], 0, 8);
    }
  }

  public Piece at(Square s) {
    return cells[s.file()][s.rank()];
  }

  public void place(Square s, Piece piece) {
    cells[s.file()][s.rank()] = piece;
  }

  public void clear(Square s) {
    cells[s.file()][s.rank()] = null;
  }

  public void move(Square from, Square to) {
    Piece p = at(from);
    if (p == null) {
      throw new IllegalArgumentException("empty from " + from);
    }
    clear(from);
    place(to, p);
  }

  public Optional<Square> findKing(Color color) {
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = cells[f][r];
        if (p instanceof King k && k.color() == color) {
          return Optional.of(new Square(f, r));
        }
      }
    }
    return Optional.empty();
  }

  /** True when {@code by} has a pseudo-legal capture on {@code target}. */
  public boolean isAttacked(Square target, Color by) {
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = cells[f][r];
        if (p == null || p.color() != by) {
          continue;
        }
        Square from = new Square(f, r);
        if (p.attacks(this, from).contains(target)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean inCheck(Color side) {
    Square king =
        findKing(side).orElseThrow(() -> new IllegalStateException("no king for " + side));
    return isAttacked(king, side.other());
  }

  public List<Square> occupiedBy(Color color) {
    List<Square> out = new ArrayList<>();
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = cells[f][r];
        if (p != null && p.color() == color) {
          out.add(new Square(f, r));
        }
      }
    }
    return out;
  }

  /** Empty board plus the usual starting setup. */
  public static Board standard() {
    Board b = new Board();
    placeBackRank(b, 0, Color.WHITE);
    placeBackRank(b, 7, Color.BLACK);
    for (int f = 0; f < 8; f++) {
      b.place(new Square(f, 1), new Pawn(Color.WHITE));
      b.place(new Square(f, 6), new Pawn(Color.BLACK));
    }
    return b;
  }

  private static void placeBackRank(Board b, int rank, Color color) {
    b.place(new Square(0, rank), new Rook(color));
    b.place(new Square(1, rank), new Knight(color));
    b.place(new Square(2, rank), new Bishop(color));
    b.place(new Square(3, rank), new Queen(color));
    b.place(new Square(4, rank), new King(color));
    b.place(new Square(5, rank), new Bishop(color));
    b.place(new Square(6, rank), new Knight(color));
    b.place(new Square(7, rank), new Rook(color));
  }

  public String render() {
    StringBuilder sb = new StringBuilder();
    for (int r = 7; r >= 0; r--) {
      sb.append(r + 1).append(' ');
      for (int f = 0; f < 8; f++) {
        Piece p = cells[f][r];
        sb.append(p == null ? '.' : p.symbol()).append(' ');
      }
      sb.append('\n');
    }
    sb.append("  a b c d e f g h");
    return sb.toString();
  }
}
