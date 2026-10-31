package patterns.behavioral.template;

/** Gotcha: a template step called from a constructor runs before the subclass is initialised. */
public final class TemplateCtorTrap {
  // ANCHOR: ctor-trap
  abstract static class Base {
    Base() {
      System.out.println("Base ctor sees " + label());
    }

    abstract String label();
  }

  static final class Child extends Base {
    private String name = "child";

    Child() {
      super();
      System.out.println("Child ctor sees " + label());
    }

    @Override
    String label() {
      return name;
    }
  }
  // ANCHOR_END: ctor-trap

  // ANCHOR: ctor-fixed
  abstract static class SafeBase {
    final void init() {
      System.out.println("init sees " + label());
    }

    abstract String label();
  }

  static final class SafeChild extends SafeBase {
    private final String name = "child";

    @Override
    String label() {
      return name;
    }
  }
  // ANCHOR_END: ctor-fixed

  public static void main(String[] args) {
    new Child();
    new SafeChild().init();
  }
}
