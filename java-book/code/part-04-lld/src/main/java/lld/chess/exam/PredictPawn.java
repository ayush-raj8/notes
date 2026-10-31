package lld.chess.exam;

import java.util.List;
import lld.chess.*;

public class PredictPawn {
  public static void main(String[] args) {
    Board b = new Board();
    b.place(Square.parse("e2"), new Pawn(Color.WHITE));
    b.place(Square.parse("d3"), new Pawn(Color.BLACK));
    b.place(Square.parse("e4"), new Pawn(Color.BLACK));
    List<Square> moves = b.at(Square.parse("e2")).pseudoLegalTargets(b, Square.parse("e2"));
    System.out.println(moves);
    System.out.println(b.at(Square.parse("e2")).attacks(b, Square.parse("e2")));
  }
}
