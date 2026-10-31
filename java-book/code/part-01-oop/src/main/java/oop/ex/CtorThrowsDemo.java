package oop.ex;

// ANCHOR: ctor-throws
public final class CtorThrowsDemo {
  static int live = 0;

  static final class Widget {
    final String name;

    Widget(String name) {
      live++;
      if (name == null || name.isEmpty()) {
        throw new IllegalArgumentException("name required");
      }
      this.name = name;
    }
  }

  static class Base {
    Base() throws java.io.IOException {
      System.out.println("Base ctor");
    }
  }

  static final class Derived extends Base {
    Derived() throws java.io.IOException {
      super();
      System.out.println("Derived ctor");
    }
  }

  public static void main(String[] args) throws Exception {
    Widget w = null;
    try {
      w = new Widget("");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(w);
    System.out.println(live);
    new Derived();
  }
}
// ANCHOR_END: ctor-throws
