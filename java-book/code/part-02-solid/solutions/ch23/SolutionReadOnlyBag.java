package solid.lsp.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 2: split read and write roles so a frozen bag never has to throw. */
public final class SolutionReadOnlyBag {
  interface ReadBag {
    int size();
  }

  interface WriteBag extends ReadBag {
    void add(String s);
  }

  record FrozenBag(List<String> items) implements ReadBag {
    public int size() {
      return items.size();
    }
  }

  static final class MutableBag implements WriteBag {
    private final List<String> items = new ArrayList<>();

    public void add(String s) {
      items.add(s);
    }

    public int size() {
      return items.size();
    }
  }

  static void fill(WriteBag b) {
    b.add("x");
  }

  public static void main(String[] args) {
    MutableBag m = new MutableBag();
    fill(m);
    System.out.println(m.size() + " " + new FrozenBag(List.of("a", "b")).size());
  }
}
