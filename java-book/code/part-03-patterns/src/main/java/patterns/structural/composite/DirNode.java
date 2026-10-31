package patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: dir-node
/** Composite: holds children of the same component type and recurses. */
public final class DirNode implements FsNode {
  private final String name;
  private final List<FsNode> children = new ArrayList<>();

  public DirNode(String name) {
    this.name = name;
  }

  public DirNode add(FsNode child) {
    if (child == this) {
      throw new IllegalArgumentException("cannot add a directory to itself");
    }
    children.add(child);
    return this;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public int size() {
    int total = 0;
    for (FsNode c : children) {
      total += c.size();
    }
    return total;
  }

  @Override
  public String render(int depth) {
    StringBuilder sb = new StringBuilder("  ".repeat(depth) + name + "/ (" + size() + ")");
    for (FsNode c : children) {
      sb.append('\n').append(c.render(depth + 1));
    }
    return sb.toString();
  }
}
// ANCHOR_END: dir-node
