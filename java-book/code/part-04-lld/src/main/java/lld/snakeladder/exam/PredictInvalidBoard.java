package lld.snakeladder.exam;

import java.util.function.Supplier;
import lld.snakeladder.*;

public class PredictInvalidBoard {
  static void attempt(Supplier<Board> s) {
    try {
      s.get();
      System.out.println("ok");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) {
    attempt(() -> Board.of(3, new Ladder(2, 5), new Snake(5, 1)));
    attempt(() -> Board.of(3, new Ladder(2, 5), new Snake(2, 1)));
    attempt(() -> Board.of(3, new Snake(9, 1)));
    attempt(() -> Board.of(3, new Ladder(2, 9)));
    attempt(() -> Board.of(1));
  }
}
