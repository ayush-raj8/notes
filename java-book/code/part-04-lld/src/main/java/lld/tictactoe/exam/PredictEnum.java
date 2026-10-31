package lld.tictactoe.exam;

import lld.tictactoe.Mark;

public class PredictEnum {
  public static void main(String[] args) {
    System.out.println(Mark.X.other());
    System.out.println(Mark.valueOf("O").ordinal());
    System.out.println(Mark.values().length);
    System.out.println(Mark.X == Mark.valueOf("X"));
  }
}
