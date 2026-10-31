package oop.memory;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: var-locals
/** {@code var} is for local variables (and loop vars) with an initializer — not for fields. */
public final class VarLocals {
  public static void main(String[] args) {
    var label = "point";
    var coords = new int[] {3, 4};
    var list = new ArrayList<String>();
    list.add(label);
    System.out.println(label + " " + coords[0] + "," + coords[1] + " size=" + list.size());
    System.out.println("list runtime type: " + list.getClass().getSimpleName());
  }

  /** Shows {@code var} still has a compile-time type (here {@code List<String>}). */
  static int localSize() {
    var names = List.of("a", "b");
    return names.size();
  }
}
// ANCHOR_END: var-locals
