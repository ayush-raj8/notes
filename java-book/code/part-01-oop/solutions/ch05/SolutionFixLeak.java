package oop.nested.solutions;

/** Exercise 4: replace an inner class that pins a large outer with a static nested copy. */
public final class SolutionFixLeak {
  private final byte[] big = new byte[1_000_000];
  private final String id;

  public SolutionFixLeak(String id) {
    this.id = id;
  }

  /** Before: {@code class Label} (inner) would keep {@code big} reachable. */
  static final class Label {
    private final String text;

    Label(String text) {
      this.text = text;
    }

    String text() {
      return text;
    }
  }

  public Label label() {
    return new Label(id);
  }

  public static void main(String[] args) throws Exception {
    Label l = new SolutionFixLeak("k9").label();
    boolean hasOuterRef = false;
    for (java.lang.reflect.Field f : Label.class.getDeclaredFields()) {
      hasOuterRef |= f.isSynthetic();
    }
    System.out.println(l.text() + " " + hasOuterRef);
  }
}
