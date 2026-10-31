package lld.fs;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;

// ANCHOR: directory-node
public final class DirectoryNode extends Node {
  private final TreeMap<String, Node> children = new TreeMap<>(); // sorted listing for free

  DirectoryNode(String name) {
    super(name);
  }

  /** Read-only, in name order. */
  public Collection<Node> children() {
    return Collections.unmodifiableCollection(children.values());
  }

  public Optional<Node> child(String name) {
    return Optional.ofNullable(children.get(name));
  }

  public boolean isEmpty() {
    return children.isEmpty();
  }

  void add(Node node) {
    children.put(node.name(), node);
    node.setParent(this);
  }

  void remove(String name) {
    Node removed = children.remove(name);
    if (removed != null) {
      removed.setParent(null);
    }
  }

  @Override
  public long size() {
    long total = 0;
    for (Node n : children.values()) {
      total += n.size();
    }
    return total;
  }
}
// ANCHOR_END: directory-node
