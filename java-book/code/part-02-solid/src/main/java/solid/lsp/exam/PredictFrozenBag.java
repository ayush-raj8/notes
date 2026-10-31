package solid.lsp.exam;

// ANCHOR: predict-frozen-bag
import java.util.ArrayList;
import java.util.List;

public final class PredictFrozenBag {
  static class Bag {
    final List<String> items = new ArrayList<>();

    void add(String s) {
      items.add(s);
    }
  }

  static class FrozenBag extends Bag {
    @Override
    void add(String s) {
      throw new UnsupportedOperationException("frozen");
    }
  }

  static void fill(Bag b) {
    b.add("a");
    b.add("b");
  }

  public static void main(String[] args) {
    Bag b = new FrozenBag();
    try {
      fill(b);
      System.out.println("filled");
    } catch (UnsupportedOperationException e) {
      System.out.println("caught " + e.getMessage());
    }
    System.out.println(b.items.size());
  }
}
// ANCHOR_END: predict-frozen-bag
