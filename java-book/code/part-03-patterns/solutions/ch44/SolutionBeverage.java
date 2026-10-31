package solutions.ch44;

/** Exercise 1: classic beverage template with a hook. */
public final class SolutionBeverage {
  abstract static class Beverage {
    final void prepare() {
      System.out.println("boil water");
      brew();
      System.out.println("pour in cup");
      if (wantsCondiments()) addCondiments();
    }

    abstract void brew();

    abstract void addCondiments();

    boolean wantsCondiments() {
      return true;
    }
  }

  static final class Tea extends Beverage {
    void brew() {
      System.out.println("steep tea bag");
    }

    void addCondiments() {
      System.out.println("add lemon");
    }
  }

  static final class BlackCoffee extends Beverage {
    void brew() {
      System.out.println("drip coffee");
    }

    void addCondiments() {
      System.out.println("add milk");
    }

    @Override
    boolean wantsCondiments() {
      return false;
    }
  }

  public static void main(String[] args) {
    new Tea().prepare();
    new BlackCoffee().prepare();
  }
}
