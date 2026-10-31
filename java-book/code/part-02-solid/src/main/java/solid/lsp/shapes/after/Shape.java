package solid.lsp.shapes.after;

// ANCHOR: shape
import java.util.List;

/**
 * Contract: area() &gt;= 0; scaled(k).area() == k * k * area() for k &gt;= 1. Every subtype keeps
 * both promises, so any client of Shape can use any subtype.
 */
public sealed interface Shape {
  int area();

  Shape scaled(int k);

  record Rect(int width, int height) implements Shape {
    @Override
    public int area() {
      return width * height;
    }

    @Override
    public Rect scaled(int k) {
      return new Rect(width * k, height * k);
    }
  }

  record Square(int side) implements Shape {
    @Override
    public int area() {
      return side * side;
    }

    @Override
    public Square scaled(int k) {
      return new Square(side * k);
    }
  }

  static int totalArea(List<? extends Shape> shapes) {
    return shapes.stream().mapToInt(Shape::area).sum();
  }
}
// ANCHOR_END: shape
