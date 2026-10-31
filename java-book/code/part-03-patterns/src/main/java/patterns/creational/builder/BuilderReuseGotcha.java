package patterns.creational.builder;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: builder-reuse-gotcha
/** Gotcha: a builder that hands out its own mutable list lets later calls change old products. */
public final class BuilderReuseGotcha {
  static final class LeakyOrder {
    final List<String> items;

    LeakyOrder(List<String> items) {
      this.items = items; // no defensive copy
    }
  }

  static final class SafeOrder {
    final List<String> items;

    SafeOrder(List<String> items) {
      this.items = List.copyOf(items); // snapshot
    }
  }

  static final class OrderBuilder {
    private final List<String> items = new ArrayList<>();

    OrderBuilder add(String item) {
      items.add(item);
      return this;
    }

    LeakyOrder buildLeaky() {
      return new LeakyOrder(items);
    }

    SafeOrder buildSafe() {
      return new SafeOrder(items);
    }
  }

  public static void main(String[] args) {
    OrderBuilder b = new OrderBuilder().add("tea");
    LeakyOrder leaky = b.buildLeaky();
    SafeOrder safe = b.buildSafe();
    b.add("cake");
    System.out.println("leaky " + leaky.items);
    System.out.println("safe  " + safe.items);
  }
}
// ANCHOR_END: builder-reuse-gotcha
