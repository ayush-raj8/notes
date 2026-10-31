package solid.lsp.shapes.step1;

// ANCHOR: step1
/** Step 1: no inheritance between Rect and Sq. Only the shared promise (area) is common. */
public final class Step1Shapes {
  public interface Shape {
    int area();
  }

  public static final class Rect implements Shape {
    private int width;
    private int height;

    public Rect(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public void setHeight(int height) {
      this.height = height;
    }

    @Override
    public int area() {
      return width * height;
    }
  }

  public static final class Sq implements Shape {
    private int side;

    public Sq(int side) {
      this.side = side;
    }

    public void setSide(int side) {
      this.side = side;
    }

    @Override
    public int area() {
      return side * side;
    }
  }
}
// ANCHOR_END: step1
