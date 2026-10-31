package solid.lsp.exam;

// ANCHOR: compile-covariant-ok
public final class CompileCovariantOk {
  static class Base {
    Number size() throws Exception {
      return 1;
    }
  }

  static class Sub extends Base {
    @Override
    public Integer size() { // wider access, narrower return, fewer exceptions: all allowed
      return 2;
    }
  }

  public static void main(String[] args) throws Exception {
    Base b = new Sub();
    System.out.println(b.size());
  }
}
// ANCHOR_END: compile-covariant-ok
