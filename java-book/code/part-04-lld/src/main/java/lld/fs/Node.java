package lld.fs;

// ANCHOR: node
/** Composite root. Sealed: a node is a file or a directory, nothing else. */
public abstract sealed class Node permits FileNode, DirectoryNode {
  private String name;
  private DirectoryNode parent;

  Node(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public DirectoryNode parent() {
    return parent;
  }

  void setName(String name) {
    this.name = name;
  }

  void setParent(DirectoryNode parent) {
    this.parent = parent;
  }

  /** Bytes for files; sum of children for directories. */
  public abstract long size();

  public String path() {
    if (parent == null) {
      return "/";
    }
    String p = parent.path();
    return (p.equals("/") ? "" : p) + "/" + name;
  }
}
// ANCHOR_END: node
