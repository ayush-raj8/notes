package lld.tictactoe.exam;

import lld.tictactoe.*;

public class PredictAfterOver {
  public static void main(String[] args) {
    Game g = new Game(new Player("A", Mark.X), new Player("B", Mark.O), 3);
    int[][] moves = {{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}};
    for (int[] m : moves) {
      g.move(new Position(m[0], m[1]));
    }
    try {
      g.move(new Position(2, 2));
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(g.board().at(new Position(2, 2)).isPresent());
  }
}
