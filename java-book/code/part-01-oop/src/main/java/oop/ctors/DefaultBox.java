package oop.ctors;

// ANCHOR: default-box
/** No constructor declared → compiler supplies a public no-arg default constructor. */
public final class DefaultBox {
  private int size;

  public int size() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
// ANCHOR_END: default-box
