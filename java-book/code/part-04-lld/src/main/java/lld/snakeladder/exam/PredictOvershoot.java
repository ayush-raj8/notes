package lld.snakeladder.exam;

import java.util.List;
import lld.snakeladder.*;

public class PredictOvershoot {
  public static void main(String[] args) {
    Board b = Board.of(5);
    // A walks 6,12,18,24 (sixes roll again), then rolls 3 (overshoot), then 1.
    Game g = new Game(b, List.of("A", "B"), new FixedDice(6, 6, 6, 6, 3, 2, 1), Rules.CLASSIC);
    for (int i = 0; i < 4; i++) {
      g.play();
    }
    System.out.println(g.play().describe());
    System.out.println(g.play().describe());
    System.out.println(g.play().describe());
    System.out.println(g.winner().map(Player::name).orElse("none"));
  }
}
