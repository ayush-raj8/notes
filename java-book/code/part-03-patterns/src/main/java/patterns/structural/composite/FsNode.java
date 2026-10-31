package patterns.structural.composite;

// ANCHOR: fs-node
/** Component: files and directories are used through the same interface. */
public interface FsNode {
  String name();

  int size();

  String render(int depth);
}
// ANCHOR_END: fs-node
