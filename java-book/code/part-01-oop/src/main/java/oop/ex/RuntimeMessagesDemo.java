package oop.ex;

import java.util.List;

// ANCHOR: runtime-messages
public final class RuntimeMessagesDemo {
  interface Action {
    void run() throws Exception;
  }

  static void show(Action a) {
    try {
      a.run();
    } catch (Exception e) {
      System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    String s = null;
    int[] arr = new int[2];
    Object o = "text";
    show(() -> s.length());
    show(() -> System.out.println(arr[2]));
    show(() -> System.out.println(10 / (arr[0])));
    show(() -> System.out.println((Integer) o));
    show(() -> Integer.parseInt("12x"));
    show(() -> "abc".substring(5));
    show(() -> List.of(1).add(2));
    show(() -> List.of(1, 2).get(5));
    show(() -> java.util.Objects.requireNonNull(null, "id must not be null"));
  }
}
// ANCHOR_END: runtime-messages
