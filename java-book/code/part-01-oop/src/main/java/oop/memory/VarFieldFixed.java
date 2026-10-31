package oop.memory;

// ANCHOR: var-field-fixed
/** Fixed form of the illegal {@code var} field: declare an explicit type. */
public final class VarFieldFixed {
  private int count = 0;

  public int inc() {
    return ++count;
  }

  public static void main(String[] args) {
    VarFieldFixed f = new VarFieldFixed();
    System.out.println(f.inc());
    System.out.println(f.inc());
  }
}
// ANCHOR_END: var-field-fixed
