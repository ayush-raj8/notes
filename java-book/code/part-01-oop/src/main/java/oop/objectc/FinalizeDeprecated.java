package oop.objectc;

// ANCHOR: finalize-deprecated
/** {@code finalize()} is deprecated for removal (Java 18+). Use try-with-resources instead. */
public final class FinalizeDeprecated {
  static final class OldStyle {
    @Override
    @SuppressWarnings("removal")
    protected void finalize() {
      // never rely on this running
    }
  }

  static final class Resource implements AutoCloseable {
    @Override
    public void close() {
      System.out.println("closed");
    }
  }

  public static void main(String[] args) {
    try (Resource r = new Resource()) {
      System.out.println("using " + (r != null));
    }
    new OldStyle();
  }
}
// ANCHOR_END: finalize-deprecated
