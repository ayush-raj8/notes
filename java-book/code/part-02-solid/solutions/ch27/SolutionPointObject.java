package solid.smells.solutions;

/** Exercise 2: six primitives in a row become three named values. */
public final class SolutionPointObject {
  static String drawLineBefore(int x1, int y1, int x2, int y2, int width, String color) {
    return "line (" + x1 + "," + y1 + ")->(" + x2 + "," + y2 + ") w=" + width + " " + color;
  }

  record Point(int x, int y) {}

  record Stroke(int width, String color) {}

  static String drawLine(Point from, Point to, Stroke stroke) {
    return "line (" + from.x() + "," + from.y() + ")->(" + to.x() + "," + to.y() + ") w=" + stroke.width() + " "
        + stroke.color();
  }

  public static void main(String[] args) {
    String before = drawLineBefore(0, 0, 3, 4, 2, "red");
    String after = drawLine(new Point(0, 0), new Point(3, 4), new Stroke(2, "red"));
    System.out.println(after);
    System.out.println(before.equals(after));
  }
}
