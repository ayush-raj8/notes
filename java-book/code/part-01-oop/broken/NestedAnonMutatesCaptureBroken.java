package oop.nested;

/** Intentionally invalid: an anonymous class cannot assign a captured local. */
public final class NestedAnonMutatesCaptureBroken {
  public static void main(String[] args) {
    int total = 0;
    Runnable r =
        new Runnable() {
          @Override
          public void run() {
            total++;
          }
        };
    r.run();
  }
}
