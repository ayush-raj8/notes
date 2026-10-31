package lld.snakeladder;

public record Snake(int from, int to) implements Jump {
  public Snake {
    if (to >= from) {
      throw new IllegalArgumentException("snake must go down: " + from + "->" + to);
    }
  }
}
