package oop.poly;

// ANCHOR: poly-upcast-downcast
/** Upcast is implicit and always safe; downcast needs a cast and a run-time check. */
public final class PolyUpcastDowncast {
  public static void main(String[] args) {
    PzDog dog = new PzDog();
    PzAnimal a = dog; // upcast: no cast needed
    Object o = a; // upcast again

    PzDog back = (PzDog) a; // downcast: the object really is a PzDog
    System.out.println(back.fetch());
    System.out.println(a == back);

    try {
      PzCat cat = (PzCat) a; // compiles; PzDog object is not a PzCat
      System.out.println(cat.sound());
    } catch (ClassCastException e) {
      System.out.println("CCE: " + e.getMessage());
    }

    System.out.println(o instanceof PzAnimal);
    System.out.println(o instanceof PzCat);
  }
}
// ANCHOR_END: poly-upcast-downcast
