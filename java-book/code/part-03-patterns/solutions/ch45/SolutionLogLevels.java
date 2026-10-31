package solutions.ch45;

/** Exercise 1: each logger link prints the message if the level is high enough, then passes it on. */
public final class SolutionLogLevels {
  abstract static class Logger {
    private Logger next;

    Logger then(Logger n) {
      next = n;
      return n;
    }

    final void log(int level, String msg) {
      if (level >= minLevel()) write(msg);
      if (next != null) next.log(level, msg);
    }

    abstract int minLevel();

    abstract void write(String msg);
  }

  static Logger of(int min, String tag) {
    return new Logger() {
      int minLevel() {
        return min;
      }

      void write(String msg) {
        System.out.println(tag + ": " + msg);
      }
    };
  }

  public static void main(String[] args) {
    Logger console = of(1, "console");
    console.then(of(2, "file")).then(of(3, "mail"));
    console.log(1, "starting");
    console.log(3, "disk full");
  }
}
