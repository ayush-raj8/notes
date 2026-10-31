package lld.chess;

public final class ChessDemo {
  public static void main(String[] args) {
    Game game = new Game();
    System.out.println(StatusText.of(game.status()));
    game.move("e2-e4");
    game.move("e7-e5");
    game.move("g1-f3");
    game.move("b8-c6");
    game.move("f1-c4");
    System.out.println(StatusText.of(game.status()));
    System.out.println("knight targets from b1: " + game.board().at(Square.parse("b1")).pseudoLegalTargets(game.board(), Square.parse("b1")));

    // Fool's mate setup on a fresh game for a short checkmate demo.
    Game fools = new Game();
    fools.move("f2-f3");
    fools.move("e7-e5");
    fools.move("g2-g4");
    GameStatus end = fools.move("d8-h4");
    System.out.println(StatusText.of(end));
    System.out.println(fools.board().inCheck(Color.WHITE));
  }
}
