package oop.memory;

// ANCHOR: this-demo
/** {@code this} disambiguates fields from parameters and supports chained calls. */
public final class ThisDemo {
  private int value;

  public ThisDemo(int value) {
    this.value = value;
  }

  public ThisDemo bump(int delta) {
    this.value += delta;
    return this;
  }

  public int value() {
    return value;
  }

  public static void main(String[] args) {
    ThisDemo t = new ThisDemo(1).bump(2).bump(3);
    System.out.println(t.value());
  }
}
// ANCHOR_END: this-demo
