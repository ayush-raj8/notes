package oop.nested;

// ANCHOR: shadow-demo
/** Same name at three levels: pick the one you want with this / Outer.this. */
public final class ShadowDemo {
  private final int x = 1;

  public final class Inner {
    private final int x = 2;

    public String show(int x) {
      return x + " " + this.x + " " + ShadowDemo.this.x;
    }
  }

  public static void main(String[] args) {
    ShadowDemo outer = new ShadowDemo();
    ShadowDemo.Inner in = outer.new Inner();
    System.out.println(in.show(3));
  }
}
// ANCHOR_END: shadow-demo
