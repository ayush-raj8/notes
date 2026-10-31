package patterns.creational.factorymethod.exam;

import java.util.Arrays;
import java.util.List;

// ANCHOR: predict-list-factories
public final class PredictListFactories {
  public static void main(String[] args) {
    List<String> fixed = Arrays.asList("a", "b");
    fixed.set(0, "z");
    System.out.println(fixed);
    try {
      fixed.add("c");
    } catch (UnsupportedOperationException e) {
      System.out.println("add: UOE");
    }
    List<String> immutable = List.of("a", "b");
    try {
      immutable.set(0, "z");
    } catch (UnsupportedOperationException e) {
      System.out.println("set: UOE");
    }
  }
}
// ANCHOR_END: predict-list-factories
