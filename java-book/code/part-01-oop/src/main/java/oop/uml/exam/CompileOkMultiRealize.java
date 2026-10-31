package oop.uml.exam;

// ANCHOR: compile-ok-multi-realize
/** Compiles: one superclass (generalization) plus several interfaces (realization). */
public final class CompileOkMultiRealize {
  interface Flyer {
    default String move() {
      return "fly";
    }
  }

  interface Singer {
    String sing();
  }

  static class Bird {
    String name() {
      return "bird";
    }
  }

  static final class Parrot extends Bird implements Flyer, Singer {
    @Override
    public String sing() {
      return "hello";
    }
  }

  public static void main(String[] args) {
    Parrot p = new Parrot();
    System.out.println(p.name() + " " + p.move() + " " + p.sing());
  }
}
// ANCHOR_END: compile-ok-multi-realize
