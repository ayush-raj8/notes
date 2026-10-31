package lld.snakeladder;

/** Source of randomness behind an interface so tests and demos can script every roll. */
@FunctionalInterface
public interface Dice {
  int roll();
}
