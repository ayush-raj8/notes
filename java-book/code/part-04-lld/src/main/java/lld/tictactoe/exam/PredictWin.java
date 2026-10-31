package lld.tictactoe.exam;

import lld.tictactoe.*;

public class PredictWin {
  public static void main(String[] args) {
    Game g = new Game(new Player("A", Mark.X), new Player("B", Mark.O), 3);
    int[][] moves = {{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}};
    for (int[] m : moves) {
      System.out.println(StatusText.of(g.move(new Position(m[0], m[1]))));
    }
  }
}
