package solid.lsp.shapes.before;

// ANCHOR: rectangle
public class Rectangle {
  protected int width;
  protected int height;

  public Rectangle(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int area() {
    return width * height;
  }
}
// ANCHOR_END: rectangle
