package oop.ctors;

// ANCHOR: only-param-fixed
/** Fixed: supply an explicit no-arg (or overload) when you still want {@code new OnlyParamFixed()}. */
public final class OnlyParamFixed {
  private final int n;

  public OnlyParamFixed() {
    this(0);
  }

  public OnlyParamFixed(int n) {
    this.n = n;
  }

  public int n() {
    return n;
  }

  public static void main(String[] args) {
    System.out.println(new OnlyParamFixed().n());
    System.out.println(new OnlyParamFixed(7).n());
  }
}
// ANCHOR_END: only-param-fixed
