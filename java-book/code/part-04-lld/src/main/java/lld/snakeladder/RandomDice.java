package lld.snakeladder;

import java.util.Random;

public final class RandomDice implements Dice {
  private final Random random;

  public RandomDice(Random random) {
    this.random = random;
  }

  @Override
  public int roll() {
    return random.nextInt(6) + 1;
  }
}
