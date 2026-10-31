package oop.override;

interface Speaks {
  String speak();
}

/** Not allowed: interface methods are public; the implementation must be public too. */
public class OverrideInterfaceAccessBroken implements Speaks {
  String speak() {
    return "hi";
  }
}
