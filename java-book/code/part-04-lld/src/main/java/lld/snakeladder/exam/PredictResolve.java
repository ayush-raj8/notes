package lld.snakeladder.exam;

import lld.snakeladder.*;

public class PredictResolve {
  public static void main(String[] args) {
    Board b = Board.of(5, new Ladder(3, 11), new Snake(14, 4));
    System.out.println(b.resolve(3));
    System.out.println(b.resolve(14));
    System.out.println(b.resolve(5));
    System.out.println(b.jumpAt(11).isPresent());
    System.out.println(b.lastCell());
  }
}
