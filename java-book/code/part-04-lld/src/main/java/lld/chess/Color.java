package lld.chess;

public enum Color {
  WHITE,
  BLACK;

  public Color other() {
    return this == WHITE ? BLACK : WHITE;
  }
}
