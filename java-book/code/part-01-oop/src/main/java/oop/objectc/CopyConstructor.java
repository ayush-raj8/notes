package oop.objectc;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: copy-ctor
/** Copy constructor: the usual alternative to Cloneable. */
public final class CopyConstructor {
  static final class Roster {
    private final List<String> names;

    Roster(List<String> names) {
      this.names = new ArrayList<>(names);
    }

    Roster(Roster other) {
      this(other.names);
    }

    void add(String n) {
      names.add(n);
    }

    @Override
    public String toString() {
      return names.toString();
    }
  }

  public static void main(String[] args) {
    Roster a = new Roster(List.of("x"));
    Roster b = new Roster(a);
    a.add("y");
    System.out.println(a + " " + b);
  }
}
// ANCHOR_END: copy-ctor
