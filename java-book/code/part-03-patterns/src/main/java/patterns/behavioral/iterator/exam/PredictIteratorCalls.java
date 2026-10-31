package patterns.behavioral.iterator.exam;

import java.util.Iterator;
import java.util.List;

// ANCHOR: predict-iterator-calls
public final class PredictIteratorCalls {
  static final class Counted implements Iterable<String> {
    private final List<String> items = List.of("x", "y");
    int created;

    @Override
    public Iterator<String> iterator() {
      created++;
      return items.iterator();
    }
  }

  public static void main(String[] args) {
    Counted c = new Counted();
    for (String a : c) {
      for (String b : c) {
        System.out.print(a + b + " ");
      }
    }
    System.out.println();
    System.out.println("iterators created: " + c.created);
  }
}
// ANCHOR_END: predict-iterator-calls
