package lld.tictactoe;

/** Immutable cell address. A record gives equals/hashCode/toString for free. */
public record Position(int row, int col) {
  public Position {
    if (row < 0 || col < 0) {
      throw new IllegalArgumentException("negative coordinate: " + row + "," + col);
    }
  }

  /** Parses "row,col" (zero-based). */
  public static Position parse(String text) {
    String[] parts = text.split(",");
    if (parts.length != 2) {
      throw new InvalidMoveException("expected row,col but got: " + text);
    }
    try {
      return new Position(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
    } catch (NumberFormatException e) {
      throw new InvalidMoveException("not a number: " + text);
    }
  }
}
