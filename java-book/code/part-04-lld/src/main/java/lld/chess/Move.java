package lld.chess;

public record Move(Square from, Square to) {
  public static Move parse(String text) {
    String[] parts = text.trim().split("\\s*-\\s*|\\s+");
    if (parts.length != 2) {
      throw new IllegalArgumentException("bad move: " + text);
    }
    return new Move(Square.parse(parts[0]), Square.parse(parts[1]));
  }

  @Override
  public String toString() {
    return from + "-" + to;
  }
}
