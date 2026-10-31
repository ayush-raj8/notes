package oop.immut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: final-vs-immutable
/** final fixes the reference; immutability is about the object. */
public final class FinalVsImmutable {
  public static void main(String[] args) {
    final List<String> fin = new ArrayList<>();
    fin.add("a");
    System.out.println("final list: " + fin);

    List<String> base = new ArrayList<>(List.of("x"));
    List<String> view = Collections.unmodifiableList(base);
    List<String> copy = List.copyOf(base);
    base.add("y");
    System.out.println("view: " + view);
    System.out.println("copy: " + copy);

    try {
      List.of("q").add("r");
    } catch (UnsupportedOperationException e) {
      System.out.println("List.of -> UnsupportedOperationException");
    }

    String s = "abc";
    s.toUpperCase();
    s.concat("!");
    System.out.println("string unchanged: " + s);
    s = s.toUpperCase();
    System.out.println("reassigned: " + s);
  }
}
// ANCHOR_END: final-vs-immutable
