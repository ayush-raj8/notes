package lld.chess.exam;

import lld.chess.*;

public class PredictFoolsMate {
  public static void main(String[] args) {
    Game g = new Game();
    g.move("f2-f3");
    g.move("e7-e5");
    g.move("g2-g4");
    System.out.println(StatusText.of(g.move("d8-h4")));
    try {
      g.move("e2-e3");
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage().startsWith("game over"));
    }
  }
}
