package oop.hello;

// ANCHOR: hello-edge
/** Edge case: empty name still produces a well-formed greeting. */
public final class HelloEdge {
  public static String greet(String name) {
    String safe = name == null || name.isBlank() ? "world" : name.trim();
    return "Hello, " + safe + ".";
  }

  public static void main(String[] args) {
    System.out.println(greet(""));
    System.out.println(greet(null));
    System.out.println(greet("  Ada  "));
  }
}
// ANCHOR_END: hello-edge
