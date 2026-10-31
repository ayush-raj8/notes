package solid.smells.solutions;

import java.util.List;

/** Exercise 5: the area rule moves from the report into the Rect it reads. */
public final class SolutionMoveArea {
  record RectData(int w, int h) {}

  static int totalAreaBefore(List<RectData> rects) {
    int total = 0;
    for (RectData r : rects) {
      total += r.w() * r.h(); // the report knows the area formula
    }
    return total;
  }

  record Rect(int w, int h) {
    int area() {
      return w * h;
    }
  }

  static int totalArea(List<Rect> rects) {
    return rects.stream().mapToInt(Rect::area).sum();
  }

  public static void main(String[] args) {
    System.out.println(totalAreaBefore(List.of(new RectData(2, 3), new RectData(4, 5))));
    System.out.println(totalArea(List.of(new Rect(2, 3), new Rect(4, 5))));
  }
}
