package oop.nested.exam;

// ANCHOR: compile-ok-outer-new
public final class CompileOkOuterNew {
  private static int shared = 1;
  private int mine = 2;

  static class Sn {
    int read() {
      return shared; // static member of outer: allowed
    }
  }

  class In {
    int read() {
      return shared + mine; // both allowed
    }
  }

  public static void main(String[] args) {
    CompileOkOuterNew o = new CompileOkOuterNew();
    CompileOkOuterNew.In in = o.new In();
    Sn sn = new Sn();
    System.out.println(in.read() + " " + sn.read());
  }
}
// ANCHOR_END: compile-ok-outer-new
