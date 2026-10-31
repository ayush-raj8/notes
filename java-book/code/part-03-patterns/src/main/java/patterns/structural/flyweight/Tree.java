package patterns.structural.flyweight;

// ANCHOR: tree
/** Context: extrinsic state (position) stays per tree; the type is a shared reference. */
public record Tree(int x, int y, TreeType type) {
  public String draw() {
    return type.draw(x, y);
  }
}
// ANCHOR_END: tree
