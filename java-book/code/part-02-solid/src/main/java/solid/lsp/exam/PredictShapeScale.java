package solid.lsp.exam;

// ANCHOR: predict-shape-scale
import java.util.List;
import solid.lsp.shapes.after.Shape;

public final class PredictShapeScale {
  public static void main(String[] args) {
    Shape r = new Shape.Rect(2, 3);
    Shape s = new Shape.Square(2);
    System.out.println(Shape.totalArea(List.of(r, s)));
    System.out.println(r.scaled(3));
    System.out.println(Shape.totalArea(List.of(r.scaled(3), s.scaled(3))));
  }
}
// ANCHOR_END: predict-shape-scale
