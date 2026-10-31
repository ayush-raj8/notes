package oop.poly;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: poly-class-cast
/** Where ClassCastException comes from, and where it does not. */
public final class PolyClassCast {
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void main(String[] args) {
    Object o = "text";
    try {
      Integer i = (Integer) o;
      System.out.println(i);
    } catch (ClassCastException e) {
      System.out.println("1 " + e.getMessage());
    }

    PzAnimal cat = new PzCat();
    try {
      PzDog d = (PzDog) cat;
      System.out.println(d);
    } catch (ClassCastException e) {
      System.out.println("2 " + e.getMessage());
    }

    PzAnimal none = null;
    PzDog nd = (PzDog) none; // casting null never fails
    System.out.println("3 " + nd);

    List raw = new ArrayList<String>();
    raw.add(1); // raw type: no check here
    List<String> strings = raw;
    try {
      String s = strings.get(0); // compiler-inserted cast to String fails here
      System.out.println(s);
    } catch (ClassCastException e) {
      System.out.println("4 " + e.getMessage());
    }
  }
}
// ANCHOR_END: poly-class-cast
