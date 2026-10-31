import java.util.EnumMap;
import java.util.Map;
import lld.tictactoe.*;

/** Exercise solutions for chapter 61. Run: java -cp target/classes:<out> Solutions61 */
public class Solutions61 {
  /** Ex 1: a 4x4 game is won by four in a column, not three. */
  static String fourByFour() {
    Game g = new Game(new Player("A", Mark.X), new Player("B", Mark.O), 4);
    String[] moves = {"0,0", "0,1", "1,0", "1,1", "2,0", "2,1", "3,0"};
    GameStatus s = g.status();
    for (String m : moves) {
      s = g.move(Position.parse(m));
    }
    return StatusText.of(s);
  }

  /** Ex 2: two bots using the same strategy play a full game. */
  static String botVsBot(MoveStrategy strategy) {
    Game g = new Game(new Player("A", Mark.X), new Player("B", Mark.O), 3);
    while (g.status() instanceof GameStatus.InProgress) {
      g.move(strategy.choose(g.board()));
    }
    return StatusText.of(g.status());
  }

  /** Ex 3: free cells = size*size minus filled; the model already exposes freeCells(). */
  static int movesLeft(Board board) {
    return board.freeCells().size();
  }

  /** Ex 4: scoreboard keyed by an enum; EnumMap is the right Map for enum keys. */
  static final class Scoreboard {
    private final Map<Mark, Integer> wins = new EnumMap<>(Mark.class);
    private int draws;

    void record(GameStatus s) {
      switch (s) {
        case GameStatus.Won(Mark m) -> wins.merge(m, 1, Integer::sum);
        case GameStatus.Draw d -> draws++;
        case GameStatus.InProgress ip -> throw new IllegalArgumentException("not finished");
      }
    }

    String summary() {
      return "X=" + wins.getOrDefault(Mark.X, 0) + " O=" + wins.getOrDefault(Mark.O, 0) + " draws=" + draws;
    }
  }

  /** Ex 5: parsing failures surface as InvalidMoveException, not NumberFormatException. */
  static String tryParse(String text) {
    try {
      return Position.parse(text).toString();
    } catch (InvalidMoveException e) {
      return e.getMessage();
    }
  }

  public static void main(String[] args) {
    System.out.println("ex1 " + fourByFour());
    System.out.println("ex2 " + botVsBot(MoveStrategy.FIRST_FREE));
    System.out.println("ex3 " + movesLeft(new Board(4)));
    Scoreboard sb = new Scoreboard();
    sb.record(new GameStatus.Won(Mark.X));
    sb.record(new GameStatus.Won(Mark.X));
    sb.record(new GameStatus.Draw());
    System.out.println("ex4 " + sb.summary());
    System.out.println("ex5 " + tryParse("2, 1"));
    System.out.println("ex5 " + tryParse("a,b"));
    System.out.println("ex5 " + tryParse("7"));
  }
}
