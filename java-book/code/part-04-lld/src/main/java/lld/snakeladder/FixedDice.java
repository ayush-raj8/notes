package lld.snakeladder;

/** Replays a scripted sequence, wrapping around. */
public final class FixedDice implements Dice {
  private final int[] values;
  private int next;

  public FixedDice(int... values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("need at least one value");
    }
    this.values = values.clone();
  }

  @Override
  public int roll() {
    int v = values[next];
    next = (next + 1) % values.length;
    return v;
  }
}
