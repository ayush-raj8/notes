package lld.snakeladder;

import java.util.List;

public final class SnakeLadderDemo {
  public static void main(String[] args) {
    Board board = Board.of(5, new Ladder(3, 11), new Ladder(6, 17), new Snake(14, 4), new Snake(22, 9));
    Game game = new Game(board, List.of("Asha", "Ben"), new FixedDice(3, 2, 6, 4, 5, 1), Rules.CLASSIC);
    int turns = 0;
    while (game.winner().isEmpty()) {
      System.out.println(game.play().describe());
      turns++;
    }
    System.out.println(game.winner().get().name() + " wins after " + turns + " rolls");
  }
}
