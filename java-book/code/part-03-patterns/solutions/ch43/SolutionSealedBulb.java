package solutions.ch43;

/** Exercise 3: sealed states for a bulb that burns out after 3 switch-ons. */
public final class SolutionSealedBulb {
  sealed interface Bulb permits Off, On, Burnt {}

  record Off(int uses) implements Bulb {}

  record On(int uses) implements Bulb {}

  record Burnt() implements Bulb {}

  static Bulb toggle(Bulb b) {
    return switch (b) {
      case Off o when o.uses() >= 3 -> new Burnt();
      case Off o -> new On(o.uses() + 1);
      case On o -> new Off(o.uses());
      case Burnt x -> x;
    };
  }

  public static void main(String[] args) {
    Bulb b = new Off(0);
    for (int i = 0; i < 8; i++) {
      b = toggle(b);
      System.out.println(b);
    }
  }
}
