package solid.lsp.shapes.after;

// ANCHOR: shapes-after
import java.util.List;
import solid.lsp.shapes.step1.Step1Shapes;

public final class ShapesAfterDemo {
  public static void main(String[] args) {
    Step1Shapes.Rect r = new Step1Shapes.Rect(1, 1);
    r.setWidth(5);
    r.setHeight(4);
    System.out.println("step1 rect " + r.area());
    Step1Shapes.Sq s = new Step1Shapes.Sq(1);
    s.setSide(4);
    System.out.println("step1 sq   " + s.area());

    List<Shape> all = List.of(new Shape.Rect(5, 4), new Shape.Square(4));
    System.out.println("after total " + Shape.totalArea(all));
    System.out.println("after x2    " + Shape.totalArea(all.stream().map(x -> x.scaled(2)).toList()));
  }
}
// ANCHOR_END: shapes-after
