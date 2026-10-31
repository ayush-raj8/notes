package lld.chess;

/** File a–h as 0–7, rank 1–8 as 0–7. Algebraic: {@code e2}. */
public record Square(int file, int rank) {
  public Square {
    if (file < 0 || file > 7 || rank < 0 || rank > 7) {
      throw new IllegalArgumentException("out of board: file=" + file + " rank=" + rank);
    }
  }

  public static Square parse(String algebraic) {
    if (algebraic == null || algebraic.length() != 2) {
      throw new IllegalArgumentException("bad square: " + algebraic);
    }
    int file = algebraic.charAt(0) - 'a';
    int rank = algebraic.charAt(1) - '1';
    return new Square(file, rank);
  }

  @Override
  public String toString() {
    return "" + (char) ('a' + file) + (char) ('1' + rank);
  }
}
