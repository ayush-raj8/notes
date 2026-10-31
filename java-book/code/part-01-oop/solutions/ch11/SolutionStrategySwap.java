package oop.compose.solutions;

/** Exercise 4: replace a Duck subclass hierarchy with an interchangeable FlyBehavior. */
public final class SolutionStrategySwap {
  interface FlyBehavior {
    String fly();
  }

  static final class Duck {
    private FlyBehavior behavior;

    Duck(FlyBehavior behavior) {
      this.behavior = behavior;
    }

    void setBehavior(FlyBehavior behavior) {
      this.behavior = behavior;
    }

    String fly() {
      return behavior.fly();
    }
  }

  public static void main(String[] args) {
    Duck d = new Duck(() -> "flap");
    System.out.println(d.fly());
    d.setBehavior(() -> "none");
    System.out.println(d.fly());
  }
}
