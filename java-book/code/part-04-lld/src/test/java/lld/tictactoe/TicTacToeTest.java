package lld.tictactoe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TicTacToeTest {
  private static Game game(int size) {
    return new Game(new Player("A", Mark.X), new Player("B", Mark.O), size);
  }

  private static GameStatus play(Game g, String... moves) {
    GameStatus last = g.status();
    for (String m : moves) {
      last = g.move(Position.parse(m));
    }
    return last;
  }

  @Test
  void rowWin() {
    assertEquals(new GameStatus.Won(Mark.X), play(game(3), "0,0", "1,0", "0,1", "1,1", "0,2"));
  }

  @Test
  void columnWin() {
    assertEquals(new GameStatus.Won(Mark.O), play(game(3), "0,0", "0,1", "1,0", "1,1", "2,2", "2,1"));
  }

  @Test
  void diagonalWin() {
    assertEquals(new GameStatus.Won(Mark.X), play(game(3), "0,0", "0,1", "1,1", "0,2", "2,2"));
  }

  @Test
  void antiDiagonalWin() {
    assertEquals(new GameStatus.Won(Mark.X), play(game(3), "0,2", "0,0", "1,1", "0,1", "2,0"));
  }

  @Test
  void drawWhenFullWithoutLine() {
    assertEquals(
        new GameStatus.Draw(),
        play(game(3), "0,0", "0,1", "0,2", "1,1", "1,0", "1,2", "2,1", "2,0", "2,2"));
  }

  @Test
  void lastCellThatCompletesLineIsAWinNotADraw() {
    // X fills the anti-diagonal on the 9th move.
    assertEquals(
        new GameStatus.Won(Mark.X),
        play(game(3), "0,0", "0,1", "0,2", "1,0", "1,1", "1,2", "2,1", "2,2", "2,0"));
  }

  @Test
  void occupiedCellRejectedAndTurnKept() {
    Game g = game(3);
    g.move(new Position(0, 0));
    InvalidMoveException e = assertThrows(InvalidMoveException.class, () -> g.move(new Position(0, 0)));
    assertEquals("cell occupied: Position[row=0, col=0]", e.getMessage());
    assertEquals("B", g.current().name());
  }

  @Test
  void outOfBoundsRejected() {
    assertThrows(InvalidMoveException.class, () -> game(3).move(new Position(3, 0)));
  }

  @Test
  void moveAfterGameOverRejected() {
    Game g = game(3);
    play(g, "0,0", "1,0", "0,1", "1,1", "0,2");
    assertThrows(IllegalStateException.class, () -> g.move(new Position(2, 2)));
  }

  @Test
  void fourByFourNeedsFour() {
    Game g = game(4);
    assertInstanceOf(GameStatus.InProgress.class, play(g, "0,0", "1,0", "0,1", "1,1", "0,2", "1,2"));
    assertEquals(new GameStatus.Won(Mark.X), play(g, "0,3"));
  }

  @Test
  void sameMarkRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Game(new Player("A", Mark.X), new Player("B", Mark.X), 3));
  }

  @Test
  void firstFreeBotsDrawOrWinDeterministically() {
    Game g = game(3);
    while (g.status() instanceof GameStatus.InProgress) {
      g.move(MoveStrategy.FIRST_FREE.choose(g.board()));
    }
    // row-major first-free: X 0,0 O 0,1 X 0,2 O 1,0 X 1,1 O 1,2 X 2,0 -> anti-diagonal
    assertEquals(new GameStatus.Won(Mark.X), g.status());
  }

  @Test
  void rendererShowsMarks() {
    Game g = game(3);
    play(g, "0,0", "1,1");
    assertEquals("X| | \n |O| \n | | \n", BoardRenderer.render(g.board()));
  }

  @Test
  void statusTextCoversAllCases() {
    assertEquals("draw", StatusText.of(new GameStatus.Draw()));
    assertEquals("winner O", StatusText.of(new GameStatus.Won(Mark.O)));
    assertEquals("next X", StatusText.of(new GameStatus.InProgress(Mark.X)));
  }
}
