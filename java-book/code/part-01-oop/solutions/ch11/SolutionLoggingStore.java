package oop.compose.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 2: forwarding wrapper that records every call. */
public final class SolutionLoggingStore {
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
    final List<String> log = new ArrayList<>();

    LoggingStore(Store inner) {
      this.inner = inner;
    }

    public void put(String s) {
      log.add("put:" + s);
      inner.put(s);
    }

    public int size() {
      log.add("size");
      return inner.size();
    }
  }

  public static void main(String[] args) {
    LoggingStore s = new LoggingStore(new ListStore());
    s.put("a");
    s.size();
    System.out.println(s.log);
  }
}
