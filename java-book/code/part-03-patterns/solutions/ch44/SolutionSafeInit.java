package solutions.ch44;

/** Exercise 5: fix the constructor-calls-overridable-step trap with an explicit init(). */
public final class SolutionSafeInit {
  abstract static class Base {
    private boolean ready;

    final void init() {
      if (ready) throw new IllegalStateException("already initialised");
      ready = true;
      System.out.println("label=" + label());
    }

    abstract String label();
  }

  static final class Child extends Base {
    private final String name;

    Child(String name) {
      this.name = name;
    }

    @Override
    String label() {
      return name;
    }
  }

  public static void main(String[] args) {
    Child c = new Child("child");
    c.init();
    try {
      c.init();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
