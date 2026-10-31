package oop.compare;

// ANCHOR: compare-shadow
/** Shadowing: a nearer declaration hides a farther one of the same name in scope. */
public final class CompareShadow {
  private int x = 10;

  int param(int x) { // parameter shadows field
    return x + this.x;
  }

  int local() {
    int x = 5; // local shadows field
    return x * 100 + this.x;
  }

  int blockScope() {
    int total = 0;
    for (int i = 0; i < 2; i++) {
      total += i;
    }
    for (int i = 10; i < 12; i++) { // earlier i is out of scope: legal
      total += i;
    }
    return total + x;
  }

  public static void main(String[] args) {
    CompareShadow s = new CompareShadow();
    System.out.println(s.param(1));
    System.out.println(s.local());
    System.out.println(s.blockScope());
  }
}
// ANCHOR_END: compare-shadow
