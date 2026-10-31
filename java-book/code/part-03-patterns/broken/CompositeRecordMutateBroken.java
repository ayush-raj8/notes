package patterns.structural.composite;

/** Intentionally invalid: record fields are private and final, so there is no way to assign them. */
public final class CompositeRecordMutateBroken {
  public static void main(String[] args) {
    FileNode f = new FileNode("a", 1);
    f.size = 5;
  }
}
