package lld.snakeladder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SnakeLadderTest {
  private static Game game(Board b, Rules r, int... dice) {
    return new Game(b, List.of("A", "B"), new FixedDice(dice), r);
  }

  @Test
  void ladderClimbs() {
    Game g = game(Board.of(5, new Ladder(3, 11)), Rules.CLASSIC, 3);
    TurnResult t = g.play();
    assertEquals(11, t.to());
    assertEquals("ladder 3->11", t.event());
  }

  @Test
  void snakeSlides() {
    Game g = game(Board.of(5, new Snake(4, 1)), Rules.CLASSIC, 4);
    TurnResult t = g.play();
    assertEquals(1, t.to());
    assertEquals("snake 4->1", t.event());
  }

  @Test
  void turnsAlternate() {
    Game g = game(Board.of(5), Rules.CLASSIC, 1);
    g.play();
    assertEquals("B", g.current().name());
    g.play();
    assertEquals("A", g.current().name());
  }

  @Test
  void sixGivesExtraTurnWhenRuleOn() {
    Game g = game(Board.of(5), Rules.CLASSIC, 6, 2);
    assertTrue(g.play().extraTurn());
    assertEquals("A", g.current().name());
    g.play();
    assertEquals("B", g.current().name());
  }

  @Test
  void sixDoesNotRepeatWhenRuleOff() {
    Game g = game(Board.of(5), new Rules(true, false), 6);
    g.play();
    assertEquals("B", g.current().name());
  }

  @Test
  void exactFinishRejectsOvershoot() {
    Game g = game(Board.of(5), new Rules(true, false), 6, 1, 6, 1, 6, 1, 6, 1, 3);
    for (int i = 0; i < 8; i++) {
      g.play(); // A: 6,12,18,24 ; B: 1,2,3,4
    }
    TurnResult t = g.play(); // A rolls 3 from 24
    assertEquals("overshoot", t.event());
    assertEquals(24, t.to());
    assertTrue(g.winner().isEmpty());
  }

  @Test
  void looseFinishWinsOnOvershoot() {
    Game g = game(Board.of(5), new Rules(false, false), 6, 1, 6, 1, 6, 1, 6, 1, 3);
    for (int i = 0; i < 8; i++) {
      g.play();
    }
    assertEquals("win", g.play().event());
    assertEquals("A", g.winner().orElseThrow().name());
  }

  @Test
  void winOnSixEndsGameWithoutExtraTurn() {
    Game g = game(Board.of(5, new Ladder(20, 25)), new Rules(true, true), 6, 6, 6, 2);
    g.play(); // 6
    g.play(); // 12
    g.play(); // 18
    TurnResult t = g.play(); // 20 -> ladder 25
    assertEquals("win", t.event());
    assertFalse(t.extraTurn());
  }

  @Test
  void playAfterGameOverRejected() {
    Game g = game(Board.of(2), new Rules(false, false), 6);
    g.play();
    assertThrows(IllegalStateException.class, g::play);
  }

  @Test
  void invalidBoardsRejected() {
    assertThrows(IllegalArgumentException.class, () -> Board.of(3, new Ladder(2, 5), new Ladder(2, 6)));
    assertThrows(IllegalArgumentException.class, () -> Board.of(3, new Ladder(2, 5), new Snake(5, 1)));
    assertThrows(IllegalArgumentException.class, () -> Board.of(3, new Snake(9, 1)));
    assertThrows(IllegalArgumentException.class, () -> Board.of(3, new Ladder(1, 5)));
    assertThrows(IllegalArgumentException.class, () -> Board.of(3, new Ladder(2, 10)));
  }

  @Test
  void jumpRecordsValidateDirection() {
    assertThrows(IllegalArgumentException.class, () -> new Snake(3, 5));
    assertThrows(IllegalArgumentException.class, () -> new Ladder(5, 3));
  }

  @Test
  void needsTwoPlayers() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Game(Board.of(5), List.of("solo"), new FixedDice(1), Rules.CLASSIC));
  }

  @Test
  void badDiceValueRejected() {
    Game g = new Game(Board.of(5), List.of("A", "B"), () -> 7, Rules.CLASSIC);
    assertThrows(IllegalStateException.class, g::play);
  }

  @Test
  void randomDiceStaysInRangeAndIsSeedDeterministic() {
    RandomDice a = new RandomDice(new Random(7));
    RandomDice b = new RandomDice(new Random(7));
    for (int i = 0; i < 500; i++) {
      int v = a.roll();
      assertTrue(v >= 1 && v <= 6);
      assertEquals(v, b.roll());
    }
  }

  @Test
  void seededGamesFinish() {
    Board board = Board.of(10, new Ladder(4, 38), new Ladder(21, 42), new Snake(97, 13), new Snake(62, 18));
    for (long seed = 0; seed < 50; seed++) {
      Game g = new Game(board, List.of("A", "B", "C"), new RandomDice(new Random(seed)), Rules.CLASSIC);
      assertNotNull(g.runToEnd(100_000));
    }
  }

  @Test
  void runToEndGuardsStuckGames() {
    // always 6: A reaches 24 then overshoots forever (exact finish) -> guard triggers
    Game g = game(Board.of(5), Rules.CLASSIC, 6);
    assertThrows(IllegalStateException.class, () -> g.runToEnd(50));
  }
}
