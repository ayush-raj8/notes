package oop.memory;

// ANCHOR: this-shadow-gotcha
/** Gotcha: a parameter shadows a field; without {@code this}, the field is untouched. */
public final class ThisShadowGotcha {
  private int value;

  public void setWrong(int value) {
    value = value; // both sides are the parameter
  }

  public void setRight(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  public static void main(String[] args) {
    ThisShadowGotcha g = new ThisShadowGotcha();
    g.setWrong(7);
    System.out.println("after setWrong: " + g.value());
    g.setRight(7);
    System.out.println("after setRight: " + g.value());
  }
}
// ANCHOR_END: this-shadow-gotcha
