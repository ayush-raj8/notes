package lld.snakeladder;

public record Ladder(int from, int to) implements Jump {
  public Ladder {
    if (to <= from) {
      throw new IllegalArgumentException("ladder must go up: " + from + "->" + to);
    }
  }
}
