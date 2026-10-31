package solid.principles.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 5: count additions by wrapping a List, not by extending ArrayList. */
public final class SolutionDelegationCount {
  static final class CountedList<E> {
    private final List<E> inner = new ArrayList<>();
    private int adds;

    void add(E e) {
      adds++;
      inner.add(e);
    }

    void addAll(List<? extends E> es) {
      adds += es.size();
      inner.addAll(es);
    }

    int adds() {
      return adds;
    }

    int size() {
      return inner.size();
    }
  }

  public static void main(String[] args) {
    CountedList<String> l = new CountedList<>();
    l.add("a");
    l.addAll(List.of("b", "c"));
    System.out.println(l.adds() + " " + l.size());
  }
}
