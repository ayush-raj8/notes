package solid.lsp.exam;

// ANCHOR: predict-rect-square
import solid.lsp.shapes.before.Rectangle;
import solid.lsp.shapes.before.Square;
import solid.lsp.shapes.before.StretchClient;

public final class PredictRectSquare {
  public static void main(String[] args) {
    Rectangle r = new Square(2);
    System.out.println(r.area());
    r.setWidth(3);
    System.out.println(r.area());
    System.out.println(StretchClient.stretchedArea(new Rectangle(2, 2)));
    System.out.println(StretchClient.stretchedArea(new Square(2)));
  }
}
// ANCHOR_END: predict-rect-square
