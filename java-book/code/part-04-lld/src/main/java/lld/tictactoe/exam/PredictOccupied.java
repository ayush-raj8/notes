package lld.tictactoe.exam;

import lld.tictactoe.*;

public class PredictOccupied {
  public static void main(String[] args) {
    Game g = new Game(new Player("A", Mark.X), new Player("B", Mark.O), 3);
    g.move(new Position(0, 0));
    try {
      g.move(new Position(0, 0));
    } catch (InvalidMoveException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(g.current().name());
    System.out.println(StatusText.of(g.status()));
  }
}
