package oop.init.exam;

// ANCHOR: compile-forward-ok
public final class CompileForwardOk {
  static {
    early = 5;
  }

  static int early = 1;

  int viaThis = this.later + 1;
  int later = 7;

  public static void main(String[] args) {
    System.out.println("early=" + early);
    CompileForwardOk o = new CompileForwardOk();
    System.out.println("viaThis=" + o.viaThis + ", later=" + o.later);
  }
}
// ANCHOR_END: compile-forward-ok
