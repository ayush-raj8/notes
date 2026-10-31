package lld.snakeladder.exam;

import lld.snakeladder.*;

public class PredictFixedDice {
  public static void main(String[] args) {
    FixedDice d = new FixedDice(1, 2, 3);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5; i++) {
      sb.append(d.roll());
    }
    System.out.println(sb);
    Dice lambda = () -> 4;
    System.out.println(lambda.roll() + lambda.roll());
    int[] script = {5, 6};
    FixedDice copy = new FixedDice(script);
    script[0] = 1;
    System.out.println(copy.roll());
    try {
      new FixedDice();
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
