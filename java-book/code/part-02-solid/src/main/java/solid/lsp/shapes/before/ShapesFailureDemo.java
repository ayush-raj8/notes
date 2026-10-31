package solid.lsp.shapes.before;

// ANCHOR: shapes-failure
public final class ShapesFailureDemo {
  public static void main(String[] args) {
    System.out.println("Rectangle " + StretchClient.stretchedArea(new Rectangle(1, 1)));
    System.out.println("Square    " + StretchClient.stretchedArea(new Square(1)));
  }
}
// ANCHOR_END: shapes-failure
