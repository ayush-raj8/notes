package patterns.structural.composite;

/** Intentionally invalid: the safe composite keeps add() off the component interface. */
public final class CompositeLeafAddBroken {
  public static void main(String[] args) {
    FsNode n = new FileNode("a", 1);
    n.add(new FileNode("b", 2));
  }
}
