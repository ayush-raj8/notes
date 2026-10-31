package oop.compose.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 3: count adds without depending on the base's addAll strategy. */
public final class SolutionRobustCounter {
  static final class Bag {
    private final List<String> items = new ArrayList<>();
    private int adds;

    void add(String s) {
      adds++;
      items.add(s);
    }

    void addAll(List<String> all) {
      for (String s : all) {
        add(s);
      }
    }

    int adds() {
      return adds;
    }
  }

  public static void main(String[] args) {
    Bag b = new Bag();
    b.addAll(List.of("x", "y", "z"));
    System.out.println(b.adds());
  }
}
