package patterns.structural.composite;

/** Intentionally invalid: children must be FsNode, not String. */
public final class CompositeWrongChildBroken {
  public static void main(String[] args) {
    new DirNode("d").add("file.txt");
  }
}
