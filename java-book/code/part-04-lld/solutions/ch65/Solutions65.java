import java.util.*;
import lld.snakeladder.*;

/** Exercise solutions for chapter 65. */
public class Solutions65 {
  /** Ex 1: biggest ladder rise using the sealed type. */
  static int biggestRise(Board b) {
    int best = 0;
    for (Jump j : b.jumps()) {
      if (j instanceof Ladder l) {
        best = Math.max(best, l.to() - l.from());
      }
    }
    return best;
  }

  /** Ex 2: deterministic simulation: seed in, winner and rolls out. */
  static String simulate(long seed) {
    Board board = Board.of(10, new Ladder(4, 38), new Snake(62, 18), new Snake(97, 13));
    Game g = new Game(board, List.of("A", "B"), new RandomDice(new Random(seed)), Rules.CLASSIC);
    int rolls = 0;
    while (g.winner().isEmpty()) {
      g.play();
      rolls++;
    }
    return g.winner().get().name() + " in " + rolls;
  }

  /** Ex 3: a Dice from any Iterator via lambda (loaded dice for demos). */
  static Dice fromIterator(Iterator<Integer> it) {
    return it::next;
  }

  /** Ex 4: count snakes without instanceof chains. */
  static long snakeCount(Board b) {
    return b.jumps().stream().filter(j -> j instanceof Snake).count();
  }

  /** Ex 5: text listing sorted by start cell, exhaustive switch, no default. */
  static String describe(Board b) {
    StringBuilder sb = new StringBuilder();
    b.jumps().stream()
        .sorted(Comparator.comparingInt(Jump::from))
        .forEach(
            j ->
                sb.append(
                    switch (j) {
                      case Snake s -> "S" + s.from() + ">" + s.to() + " ";
                      case Ladder l -> "L" + l.from() + ">" + l.to() + " ";
                    }));
    return sb.toString().trim();
  }

  public static void main(String[] args) {
    Board b = Board.of(5, new Ladder(3, 11), new Snake(14, 4), new Ladder(6, 20), new Snake(22, 9));
    System.out.println("ex1 " + biggestRise(b));
    System.out.println("ex2 " + simulate(42));
    Dice d = fromIterator(List.of(2, 4, 6).iterator());
    System.out.println("ex3 " + d.roll() + d.roll() + d.roll());
    System.out.println("ex4 " + snakeCount(b));
    System.out.println("ex5 " + describe(b));
  }
}
