package oop.contracts;

// ANCHOR: null-resource
public final class NullResourceEdge {
  public static void main(String[] args) throws Exception {
    try (AutoCloseable r = null) {
      System.out.println("body runs");
    }
    System.out.println("done");

    AutoCloseable existing = () -> System.out.println("closed existing");
    try (existing) {
      System.out.println("java 9 form");
    }
  }
}
// ANCHOR_END: null-resource
