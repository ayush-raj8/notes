package patterns.structural.flyweight;

// ANCHOR: tree-type
/** Flyweight: immutable intrinsic state (name, color) shared by many trees. */
public final class TreeType {
  final String name;
  final String color;

  TreeType(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public String name() {
    return name;
  }

  public String draw(int x, int y) {
    return name + "/" + color + " at (" + x + "," + y + ")";
  }
}
// ANCHOR_END: tree-type
