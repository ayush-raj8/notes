package solid.lsp.shapes.before;

// ANCHOR: square
public class Square extends Rectangle {
  public Square(int side) {
    super(side, side);
  }

  @Override
  public void setWidth(int w) {
    width = w;
    height = w; // keeps the square invariant, breaks Rectangle's contract
  }

  @Override
  public void setHeight(int h) {
    width = h;
    height = h;
  }
}
// ANCHOR_END: square
