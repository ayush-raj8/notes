package lld.chess.exam;

import lld.chess.*;

public class PredictKnight {
  public static void main(String[] args) {
    Board b = new Board();
    b.place(Square.parse("b1"), new Knight(Color.WHITE));
    b.place(Square.parse("a3"), new Pawn(Color.WHITE));
    b.place(Square.parse("c3"), new Pawn(Color.BLACK));
    System.out.println(b.at(Square.parse("b1")).pseudoLegalTargets(b, Square.parse("b1")));
  }
}
