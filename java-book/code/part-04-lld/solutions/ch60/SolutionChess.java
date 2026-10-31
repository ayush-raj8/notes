import java.util.List;
import lld.chess.*;

/** Exercises 1-5 of chapter 60. */
public class SolutionChess {
  // Ex 1: algebraic helpers.
  static String roundTrip(String alg) {
    return Square.parse(alg).toString();
  }

  // Ex 2: count legal moves from a custom position.
  static int legalCount(Board board, Color turn) {
    return new Game(board, turn).legalMoves().size();
  }

  // Ex 3: does this side's king sit in check?
  static boolean whiteInCheck(Board board) {
    return board.inCheck(Color.WHITE);
  }

  // Ex 4: play a short sequence and report status text.
  static String after(String... moves) {
    Game g = new Game();
    GameStatus last = g.status();
    for (String m : moves) {
      last = g.move(m);
    }
    return StatusText.of(last);
  }

  public static void main(String[] args) {
    System.out.println(roundTrip("e4"));

    Board pin = new Board();
    pin.place(Square.parse("e1"), new King(Color.WHITE));
    pin.place(Square.parse("e2"), new Rook(Color.WHITE));
    pin.place(Square.parse("e8"), new Rook(Color.BLACK));
    System.out.println(legalCount(pin, Color.WHITE));
    System.out.println(whiteInCheck(pin));

    System.out.println(after("f2-f3", "e7-e5", "g2-g4", "d8-h4"));

    // Ex 5: sealed switch over GameStatus without default.
    GameStatus s = new GameStatus.InProgress(Color.BLACK, false);
    String kind =
        switch (s) {
          case GameStatus.InProgress p -> "in-progress";
          case GameStatus.Checkmate c -> "mate";
          case GameStatus.Stalemate st -> "stale";
        };
    System.out.println(kind);
  }
}
