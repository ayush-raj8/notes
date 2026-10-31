package patterns.structural.composite;

// ANCHOR: file-node
/** Leaf: no children, so no add(). */
public record FileNode(String name, int size) implements FsNode {
  @Override
  public String render(int depth) {
    return "  ".repeat(depth) + name + " (" + size + ")";
  }
}
// ANCHOR_END: file-node
