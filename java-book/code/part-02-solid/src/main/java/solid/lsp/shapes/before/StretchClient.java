package solid.lsp.shapes.before;

// ANCHOR: stretch-client
public final class StretchClient {
  /** Written against Rectangle's contract: width and height are independent. */
  public static int stretchedArea(Rectangle r) {
    r.setWidth(5);
    r.setHeight(4);
    return r.area(); // contract says 20
  }
}
// ANCHOR_END: stretch-client
