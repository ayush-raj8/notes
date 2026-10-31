package solutions.ch43;

/** Exercise 5: enum state with a constructor field instead of an abstract method. */
public final class SolutionEnumDuration {
  enum Phase {
    WORK(25),
    BREAK(5),
    LONG_BREAK(15);

    final int minutes;

    Phase(int minutes) {
      this.minutes = minutes;
    }

    Phase next() {
      return switch (this) {
        case WORK -> BREAK;
        case BREAK, LONG_BREAK -> WORK;
      };
    }
  }

  public static void main(String[] args) {
    Phase p = Phase.WORK;
    int total = 0;
    for (int i = 0; i < 4; i++) {
      total += p.minutes;
      p = p.next();
    }
    System.out.println(p + " " + total);
  }
}
