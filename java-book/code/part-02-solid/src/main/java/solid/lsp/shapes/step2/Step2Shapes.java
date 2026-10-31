package solid.lsp.shapes.step2;

// ANCHOR: step2
/** Step 2: immutable values. "Set one side" becomes "make a new shape". */
public final class Step2Shapes {
  public interface Shape {
    int area();
  }

  public record Rect(int width, int height) implements Shape {
    public Rect withWidth(int w) {
      return new Rect(w, height);
    }

    public Rect withHeight(int h) {
      return new Rect(width, h);
    }

    @Override
    public int area() {
      return width * height;
    }
  }

  public record Sq(int side) implements Shape {
    public Sq withSide(int s) {
      return new Sq(s);
    }

    @Override
    public int area() {
      return side * side;
    }
  }
}
// ANCHOR_END: step2
