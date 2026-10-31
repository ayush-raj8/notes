package lld.chess.exam;

import lld.chess.*;

public class PredictRookBlocked {
  public static void main(String[] args) {
    Board b = new Board();
    b.place(Square.parse("a1"), new Rook(Color.WHITE));
    b.place(Square.parse("a3"), new Pawn(Color.WHITE));
    b.place(Square.parse("c1"), new Pawn(Color.BLACK));
    System.out.println(b.at(Square.parse("a1")).pseudoLegalTargets(b, Square.parse("a1")));
  }
}
