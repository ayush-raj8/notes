package oop.coll.exam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

// ANCHOR: compile-diamond-ok
public final class CompileDiamondOk {
  public static void main(String[] args) {
    List<String> names = new ArrayList<>(); // diamond: type argument inferred
    var index = new HashMap<String, List<Integer>>(); // var: inferred from the right side
    index.computeIfAbsent("a", k -> new ArrayList<>()).add(1);
    names.add("x");

    int base = 10; // effectively final: legal to capture
    int total = IntStream.of(1, 2, 3).map(n -> n + base).sum(); // sum() exists on IntStream
    Map.Entry<String, List<Integer>> e = index.entrySet().iterator().next();
    List<? extends Number> readOnly = List.of(1, 2.5);
    Number first = readOnly.get(0); // reading from ? extends is fine

    System.out.println(names + " " + e + " " + total + " " + first);
  }
}
// ANCHOR_END: compile-diamond-ok
