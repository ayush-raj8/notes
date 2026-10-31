package solid.lsp;

/** The return type may be narrowed (covariant), never widened. */
public final class LspReturnTypeBroken {
  static class Base {
    Number size() {
      return 1;
    }
  }

  static class Sub extends Base {
    Object size() {
      return "one";
    }
  }
}
