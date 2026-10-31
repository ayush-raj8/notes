package lld.chess.exam;

import lld.chess.*;

public class PredictCheckFilter {
  public static void main(String[] args) {
    Board b = new Board();
    b.place(Square.parse("e1"), new King(Color.WHITE));
    b.place(Square.parse("e2"), new Rook(Color.WHITE));
    b.place(Square.parse("e8"), new Rook(Color.BLACK));
    Game g = new Game(b, Color.WHITE);
    try {
      g.move("e2-d2");
      System.out.println("moved");
    } catch (InvalidMoveException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(g.board().at(Square.parse("e2")).letter());
    System.out.println(StatusText.of(g.status()));
  }
}
