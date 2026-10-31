package lld.snakeladder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** size x size cells numbered 1..size*size plus validated snakes and ladders. Immutable. */
public final class Board {
  private final int lastCell;
  private final Map<Integer, Jump> byFrom = new HashMap<>();
  private final List<Jump> jumps;

  public Board(int size, List<Jump> jumps) {
    if (size < 2) {
      throw new IllegalArgumentException("size must be >= 2");
    }
    this.lastCell = size * size;
    Set<Integer> targets = new HashSet<>();
    for (Jump j : jumps) {
      if (j.from() < 2 || j.from() >= lastCell || j.to() < 1 || j.to() > lastCell) {
        throw new IllegalArgumentException("jump out of range: " + j);
      }
      if (byFrom.put(j.from(), j) != null) {
        throw new IllegalArgumentException("two jumps start at " + j.from());
      }
      targets.add(j.to());
    }
    for (Jump j : jumps) {
      if (targets.contains(j.from())) {
        throw new IllegalArgumentException("jump chain at " + j.from());
      }
    }
    this.jumps = List.copyOf(jumps);
  }

  public static Board of(int size, Jump... jumps) {
    return new Board(size, List.of(jumps));
  }

  public int lastCell() {
    return lastCell;
  }

  public List<Jump> jumps() {
    return jumps;
  }

  public Optional<Jump> jumpAt(int cell) {
    return Optional.ofNullable(byFrom.get(cell));
  }

  /** Cell where you end up after landing on {@code cell}. */
  public int resolve(int cell) {
    return jumpAt(cell).map(Jump::to).orElse(cell);
  }
}
