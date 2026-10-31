package solid.smells.solutions;

/** Exercise 6: an int that must be 0..100 becomes a type that cannot be anything else. */
public final class SolutionPercentage {
  record Percentage(int value) {
    Percentage {
      if (value < 0 || value > 100) {
        throw new IllegalArgumentException("percentage out of range: " + value);
      }
    }

    int of(int amount) {
      return amount * value / 100;
    }
  }

  public static void main(String[] args) {
    System.out.println(new Percentage(15).of(2000));
    try {
      new Percentage(150);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
