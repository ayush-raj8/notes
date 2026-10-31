package oop.ctors;

// ANCHOR: copy-edge
/** Edge: copy constructor copies the reference field as-is (shallow). Mutating the shared array
 * is visible through both accounts' holder — here we copy an int[] balance history. */
public final class CopyEdge {
  private final int[] history;

  public CopyEdge(int[] history) {
    this.history = history;
  }

  public CopyEdge(CopyEdge other) {
    this.history = other.history;
  }

  public void set(int index, int value) {
    history[index] = value;
  }

  public int get(int index) {
    return history[index];
  }

  public static void main(String[] args) {
    int[] h = {1, 2};
    CopyEdge a = new CopyEdge(h);
    CopyEdge b = new CopyEdge(a);
    b.set(0, 99);
    System.out.println(a.get(0) + " " + b.get(0));
  }
}
// ANCHOR_END: copy-edge
