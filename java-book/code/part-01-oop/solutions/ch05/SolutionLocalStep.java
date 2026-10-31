package oop.nested.solutions;

/** Exercise 3: local class capturing an effectively final parameter. */
public final class SolutionLocalStep {
  interface Counter {
    int next();
  }

  static Counter counterFrom(int start, int step) {
    class Stepper implements Counter {
      private int cur = start;

      @Override
      public int next() {
        int out = cur;
        cur += step;
        return out;
      }
    }
    return new Stepper();
  }

  public static void main(String[] args) {
    Counter c = counterFrom(10, 5);
    System.out.println(c.next() + " " + c.next() + " " + c.next());
  }
}
