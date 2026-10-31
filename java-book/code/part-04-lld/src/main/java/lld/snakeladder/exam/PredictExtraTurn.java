package lld.snakeladder.exam;

import java.util.List;
import lld.snakeladder.*;

public class PredictExtraTurn {
  public static void main(String[] args) {
    Board b = Board.of(5);
    Game g = new Game(b, List.of("A", "B"), new FixedDice(6, 2, 3), Rules.CLASSIC);
    for (int i = 0; i < 4; i++) {
      TurnResult r = g.play();
      System.out.println(r.player() + " " + r.roll() + " extra=" + r.extraTurn() + " next=" + g.current().name());
    }
    Game noBonus = new Game(b, List.of("A", "B"), new FixedDice(6), new Rules(true, false));
    noBonus.play();
    System.out.println(noBonus.current().name());
  }
}
