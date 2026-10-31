package oop.immut;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: record-compact
/** Compact canonical constructors validate and normalise parameters; fields are assigned after. */
public final class RecordCompact {
  record Range(int lo, int hi) {
    Range {
      if (lo > hi) {
        throw new IllegalArgumentException("lo > hi: " + lo + " > " + hi);
      }
    }
  }

  record Name(String value) {
    Name {
      value = value.trim();
    }
  }

  record Tags(List<String> items) {
    Tags {
      items = List.copyOf(items);
    }
  }

  public static void main(String[] args) {
    System.out.println(new Range(1, 5));
    try {
      new Range(5, 1);
    } catch (IllegalArgumentException e) {
      System.out.println("rejected: " + e.getMessage());
    }

    System.out.println(new Name("  ada  "));
    System.out.println("equal after trim: " + new Name(" x ").equals(new Name("x")));

    List<String> src = new ArrayList<>(List.of("a"));
    Tags t = new Tags(src);
    src.add("b");
    System.out.println(t);
  }
}
// ANCHOR_END: record-compact
