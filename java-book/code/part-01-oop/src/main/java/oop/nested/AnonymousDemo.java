package oop.nested;

import java.util.function.IntSupplier;

// ANCHOR: anonymous-demo
/** An anonymous class is a one-off subclass or implementation with no name. */
public final class AnonymousDemo {
  interface Greeter {
    String greet(String who);
  }

  abstract static class Shape {
    final String tag;

    Shape(String tag) {
      this.tag = tag;
    }

    abstract int area();
  }

  public static void main(String[] args) {
    String prefix = "Hello"; // effectively final
    Greeter g =
        new Greeter() {
          @Override
          public String greet(String who) {
            return prefix + ", " + who;
          }
        };
    System.out.println(g.greet("Bo"));

    Shape sq =
        new Shape("sq") { // ctor args go to the superclass constructor
          @Override
          int area() {
            return 16;
          }
        };
    System.out.println(sq.tag + " " + sq.area());

    IntSupplier counter =
        new IntSupplier() {
          private int n; // anonymous classes may have their own fields

          @Override
          public int getAsInt() {
            return ++n;
          }
        };
    System.out.println(counter.getAsInt() + " " + counter.getAsInt());
  }
}
// ANCHOR_END: anonymous-demo
