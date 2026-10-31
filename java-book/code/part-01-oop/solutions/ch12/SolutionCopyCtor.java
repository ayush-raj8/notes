package oop.objectc.solutions;

/** Exercise 4: copy constructor instead of clone. */
public final class SolutionCopyCtor {
  static final class Range {
    private final int lo;
    private final int hi;

    Range(int lo, int hi) {
      this.lo = lo;
      this.hi = hi;
    }

    Range(Range other) {
      this(other.lo, other.hi);
    }

    @Override
    public String toString() {
      return lo + ".." + hi;
    }
  }

  public static void main(String[] args) {
    System.out.println(new Range(new Range(1, 5)));
  }
}
