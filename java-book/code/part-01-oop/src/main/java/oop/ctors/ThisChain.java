package oop.ctors;

// ANCHOR: this-chain
/** {@code this(...)} must be the first statement; delegates to another constructor of this class. */
public final class ThisChain {
  private final int x;
  private final int y;

  public ThisChain() {
    this(0);
  }

  public ThisChain(int x) {
    this(x, x);
  }

  public ThisChain(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  public static void main(String[] args) {
    System.out.println(new ThisChain());
    System.out.println(new ThisChain(3));
    System.out.println(new ThisChain(1, 2));
  }
}
// ANCHOR_END: this-chain
