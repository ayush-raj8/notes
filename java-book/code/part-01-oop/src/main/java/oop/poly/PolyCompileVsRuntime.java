package oop.poly;

// ANCHOR: poly-compile-vs-runtime
/** Overloads are picked by the static type; overrides run by the object's class. */
public final class PolyCompileVsRuntime {
  static String describe(PzAnimal a) {
    return "animal";
  }

  static String describe(PzDog d) {
    return "dog";
  }

  public static void main(String[] args) {
    PzAnimal a = new PzDog(); // static type PzAnimal, runtime type PzDog
    System.out.println(describe(a));
    System.out.println(describe((PzDog) a));
    System.out.println(a.sound());
    System.out.println(a.getClass().getSimpleName());

    PzAnimal[] zoo = {new PzDog(), new PzCat(), new PzAnimal()};
    for (PzAnimal z : zoo) {
      System.out.print(z.sound() + " ");
    }
    System.out.println();
  }
}
// ANCHOR_END: poly-compile-vs-runtime
