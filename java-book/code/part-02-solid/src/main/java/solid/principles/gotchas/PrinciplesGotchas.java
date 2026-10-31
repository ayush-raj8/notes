package solid.principles.gotchas;

import java.util.Stack;

public final class PrinciplesGotchas {
  // ANCHOR: g1-coincidence
  /** Equal today by coincidence, different reasons to change. Merging them couples them. */
  static final int MAX_USERS = 100;

  static final int MAX_RETRIES = 100;

  static final int HUNDRED = 100; // "DRY": both use this; now changing one changes both
  // ANCHOR_END: g1-coincidence

  // ANCHOR: g2-clever
  /** Shortest is not simplest. */
  static String gradeClever(int s) {
    return "FDCBAA".charAt(Math.min(s / 10 - 5 < 0 ? 0 : s / 10 - 5, 5)) + "";
  }

  static String gradePlain(int s) {
    if (s >= 90) {
      return "A";
    } else if (s >= 80) {
      return "B";
    } else if (s >= 70) {
      return "C";
    } else if (s >= 60) {
      return "D";
    }
    return "F";
  }
  // ANCHOR_END: g2-clever

  // ANCHOR: g4-stack
  /** java.util.Stack extends Vector, so a "stack" can be indexed and inserted into anywhere. */
  static String stackSequence() {
    Stack<Integer> s = new Stack<>();
    s.push(1);
    s.push(2);
    s.add(1, 99); // legal: it is a Vector
    return s.pop() + " " + s.pop() + " " + s.pop();
  }
  // ANCHOR_END: g4-stack

  // ANCHOR: g3-fluent
  /** Many dots, one object: a fluent chain on the same type is not a Demeter violation. */
  static String fluent() {
    return new StringBuilder().append("a").append(1).append('x').reverse().toString();
  }
  // ANCHOR_END: g3-fluent

  // ANCHOR: g5-leaky-getter
  /** A getter that returns the internal list is a public field with extra steps. */
  static final class Bag {
    private final java.util.List<String> items = new java.util.ArrayList<>(java.util.List.of("pen"));

    java.util.List<String> leaky() {
      return items;
    }

    java.util.List<String> safe() {
      return java.util.List.copyOf(items);
    }

    int size() {
      return items.size();
    }
  }

  static String leakyGetter() {
    Bag b = new Bag();
    b.leaky().add("ink");
    int afterLeaky = b.size();
    try {
      b.safe().add("cap");
    } catch (UnsupportedOperationException e) {
      return afterLeaky + " " + b.size() + " copy-is-read-only";
    }
    return afterLeaky + " " + b.size();
  }
  // ANCHOR_END: g5-leaky-getter

  public static void main(String[] args) {
    // ANCHOR: g-main
    System.out.println("G1 " + (MAX_USERS == MAX_RETRIES) + " " + HUNDRED);
    System.out.println("G2 " + gradeClever(95) + gradeClever(85) + gradeClever(75) + gradeClever(65) + gradeClever(10)
        + " vs " + gradePlain(95) + gradePlain(85) + gradePlain(75) + gradePlain(65) + gradePlain(10));
    System.out.println("G3 " + fluent());
    System.out.println("G4 " + stackSequence());
    System.out.println("G5 " + leakyGetter());
    // ANCHOR_END: g-main
  }
}
