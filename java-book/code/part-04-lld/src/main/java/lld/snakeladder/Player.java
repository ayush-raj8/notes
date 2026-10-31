package lld.snakeladder;

/** Mutable position owned by the game. Starts off-board at 0. */
public final class Player {
  private final String name;
  private int position;

  public Player(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public int position() {
    return position;
  }

  void moveTo(int cell) {
    this.position = cell;
  }
}
