package oop.compose.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: compile-ok-delegate
/** Compiles: implements the interface and forwards. */
public final class CompileOkDelegate {
  interface Store {
    void put(String s);

    int size();
  }

  static final class ListStore implements Store {
    private final List<String> items = new ArrayList<>();

    public void put(String s) {
      items.add(s);
    }

    public int size() {
      return items.size();
    }
  }

  static final class LoggingStore implements Store {
    private final Store inner;

    LoggingStore(Store inner) {
      this.inner = inner;
    }

    public void put(String s) {
      System.out.println("put " + s);
      inner.put(s);
    }

    public int size() {
      return inner.size();
    }
  }

  public static void main(String[] args) {
    Store s = new LoggingStore(new ListStore());
    s.put("x");
    System.out.println(s.size());
  }
}
// ANCHOR_END: compile-ok-delegate
