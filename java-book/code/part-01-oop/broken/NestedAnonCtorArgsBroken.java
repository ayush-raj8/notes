package oop.nested;

/** Intentionally invalid: an anonymous class implementing an interface takes no constructor arguments. */
public final class NestedAnonCtorArgsBroken {
  public static void main(String[] args) {
    Runnable r =
        new Runnable(5) {
          @Override
          public void run() {}
        };
    r.run();
  }
}
