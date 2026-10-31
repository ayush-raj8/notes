package lld.tictactoe.exam;

import lld.tictactoe.*;

public class PredictDraw {
  public static void main(String[] args) {
    Game g = new Game(new Player("A", Mark.X), new Player("B", Mark.O), 3);
    String[] moves = {"0,0", "0,1", "0,2", "1,1", "1,0", "1,2", "2,1", "2,0", "2,2"};
    GameStatus last = null;
    for (String m : moves) {
      last = g.move(Position.parse(m));
    }
    System.out.println(StatusText.of(last));
    System.out.println(g.board().freeCells().size());
  }
}
