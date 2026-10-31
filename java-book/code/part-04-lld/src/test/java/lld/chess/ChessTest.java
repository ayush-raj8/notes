package lld.chess;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

// ANCHOR: chess-test
class ChessTest {
  @Test
  void pawnDoubleStepAndDiagonalCapture() {
    Board b = new Board();
    b.place(Square.parse("e2"), new Pawn(Color.WHITE));
    b.place(Square.parse("d3"), new Pawn(Color.BLACK));
    assertEquals(
        List.of(Square.parse("e3"), Square.parse("e4"), Square.parse("d3")),
        b.at(Square.parse("e2")).pseudoLegalTargets(b, Square.parse("e2")));
    assertEquals(
        List.of(Square.parse("d3"), Square.parse("f3")),
        b.at(Square.parse("e2")).attacks(b, Square.parse("e2")));
  }

  @Test
  void rookStopsAtOwnPieceAndCapturesEnemy() {
    Board b = new Board();
    b.place(Square.parse("a1"), new Rook(Color.WHITE));
    b.place(Square.parse("a3"), new Pawn(Color.WHITE));
    b.place(Square.parse("c1"), new Pawn(Color.BLACK));
    List<Square> moves = b.at(Square.parse("a1")).pseudoLegalTargets(b, Square.parse("a1"));
    assertTrue(moves.contains(Square.parse("a2")));
    assertFalse(moves.contains(Square.parse("a3")));
    assertFalse(moves.contains(Square.parse("a4")));
    assertTrue(moves.contains(Square.parse("b1")));
    assertTrue(moves.contains(Square.parse("c1")));
    assertFalse(moves.contains(Square.parse("d1")));
  }

  @Test
  void knightJumpsAndSkipsOwnSquare() {
    Board b = new Board();
    b.place(Square.parse("b1"), new Knight(Color.WHITE));
    b.place(Square.parse("a3"), new Pawn(Color.WHITE));
    b.place(Square.parse("c3"), new Pawn(Color.BLACK));
    List<Square> moves = b.at(Square.parse("b1")).pseudoLegalTargets(b, Square.parse("b1"));
    assertFalse(moves.contains(Square.parse("a3")));
    assertTrue(moves.contains(Square.parse("c3")));
    assertTrue(moves.contains(Square.parse("d2")));
  }

  @Test
  void kingCannotStepAdjacentToEnemyKing() {
    Board b = new Board();
    b.place(Square.parse("e4"), new King(Color.WHITE));
    b.place(Square.parse("e6"), new King(Color.BLACK));
    Game g = new Game(b, Color.WHITE);
    assertThrows(InvalidMoveException.class, () -> g.move("e4-e5"));
  }

  @Test
  void pinnedPieceCannotMoveOffPinLine() {
    Board b = new Board();
    b.place(Square.parse("e1"), new King(Color.WHITE));
    b.place(Square.parse("e2"), new Rook(Color.WHITE));
    b.place(Square.parse("e8"), new Rook(Color.BLACK));
    Game g = new Game(b, Color.WHITE);
    InvalidMoveException e = assertThrows(InvalidMoveException.class, () -> g.move("e2-d2"));
    assertTrue(e.getMessage().contains("check"));
    assertInstanceOf(Rook.class, g.board().at(Square.parse("e2")));
  }

  @Test
  void foolsMateEndsInCheckmate() {
    Game g = new Game();
    g.move("f2-f3");
    g.move("e7-e5");
    g.move("g2-g4");
    assertEquals(new GameStatus.Checkmate(Color.BLACK), g.move("d8-h4"));
    assertThrows(IllegalStateException.class, () -> g.move("e2-e3"));
  }

  @Test
  void standardOpeningMovesAreLegal() {
    Game g = new Game();
    assertInstanceOf(GameStatus.InProgress.class, g.move("e2-e4"));
    assertInstanceOf(GameStatus.InProgress.class, g.move("e7-e5"));
    assertInstanceOf(GameStatus.InProgress.class, g.move("g1-f3"));
  }

  @Test
  void pawnPromotesToQueen() {
    Board b = new Board();
    b.place(Square.parse("e7"), new Pawn(Color.WHITE));
    b.place(Square.parse("a1"), new King(Color.WHITE));
    b.place(Square.parse("h8"), new King(Color.BLACK));
    Game g = new Game(b, Color.WHITE);
    g.move("e7-e8");
    assertInstanceOf(Queen.class, g.board().at(Square.parse("e8")));
  }

  @Test
  void stalemateWhenNoLegalMoveAndNotInCheck() {
    Board b = new Board();
    b.place(Square.parse("a8"), new King(Color.BLACK));
    b.place(Square.parse("b6"), new King(Color.WHITE));
    b.place(Square.parse("c7"), new Queen(Color.WHITE));
    Game g = new Game(b, Color.BLACK);
    assertEquals(new GameStatus.Stalemate(), g.status());
  }
}
// ANCHOR_END: chess-test
