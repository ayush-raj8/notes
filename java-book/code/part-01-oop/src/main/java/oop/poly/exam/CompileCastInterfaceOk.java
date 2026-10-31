package oop.poly.exam;

// ANCHOR: compile-cast-interface-ok
class OkAnimal {}

/** Compiles: OkAnimal is not final, so a subclass could implement Runnable. Fails at run time. */
public final class CompileCastInterfaceOk {
  public static void main(String[] args) {
    OkAnimal a = new OkAnimal();
    try {
      Runnable r = (Runnable) a;
      r.run();
    } catch (ClassCastException e) {
      System.out.println("CCE at run time");
    }
  }
}
// ANCHOR_END: compile-cast-interface-ok
