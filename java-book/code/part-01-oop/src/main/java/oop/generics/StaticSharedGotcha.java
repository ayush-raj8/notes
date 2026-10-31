package oop.generics;

// ANCHOR: static-shared
/** One class, one set of statics: Box<String> and Box<Integer> are the same class at run time. */
public final class StaticSharedGotcha {
  static final class Box<T> {
    static int created;

    Box() {
      created++;
    }
  }

  public static void main(String[] args) {
    new Box<String>();
    new Box<Integer>();
    new Box<String>();
    System.out.println(Box.created);
    System.out.println(new Box<String>().getClass() == new Box<Integer>().getClass());
  }
}
// ANCHOR_END: static-shared
