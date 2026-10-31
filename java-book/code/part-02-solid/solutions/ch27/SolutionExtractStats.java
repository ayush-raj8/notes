package solid.smells.solutions;

/** Exercise 1: extract min, max and mean from one long method; the output must not change. */
public final class SolutionExtractStats {
  static String summaryInline(int[] xs) {
    int min = xs[0];
    int max = xs[0];
    long sum = 0;
    for (int x : xs) {
      if (x < min) {
        min = x;
      }
      if (x > max) {
        max = x;
      }
      sum += x;
    }
    return "min=" + min + " max=" + max + " mean=" + (double) sum / xs.length;
  }

  static int min(int[] xs) {
    int m = xs[0];
    for (int x : xs) {
      m = Math.min(m, x);
    }
    return m;
  }

  static int max(int[] xs) {
    int m = xs[0];
    for (int x : xs) {
      m = Math.max(m, x);
    }
    return m;
  }

  static double mean(int[] xs) {
    long sum = 0;
    for (int x : xs) {
      sum += x;
    }
    return (double) sum / xs.length;
  }

  static String summary(int[] xs) {
    return "min=" + min(xs) + " max=" + max(xs) + " mean=" + mean(xs);
  }

  public static void main(String[] args) {
    int[] xs = {4, 9, 1, 6};
    System.out.println(summary(xs));
    System.out.println(summary(xs).equals(summaryInline(xs)));
  }
}
