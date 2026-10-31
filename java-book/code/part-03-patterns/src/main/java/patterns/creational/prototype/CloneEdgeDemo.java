package patterns.creational.prototype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ANCHOR: clone-edge-demo
public final class CloneEdgeDemo {
  /** Does NOT implement Cloneable. */
  static final class Plain {
    Object tryClone() throws CloneNotSupportedException {
      return super.clone();
    }
  }

  public static void main(String[] args) {
    try {
      new Plain().tryClone();
    } catch (CloneNotSupportedException e) {
      System.out.println("CloneNotSupportedException");
    }

    int[][] grid = {{1, 2}, {3, 4}};
    int[][] copy = grid.clone();
    copy[0][0] = 99;
    copy[1] = new int[] {7, 7};
    System.out.println(Arrays.deepToString(grid));
    System.out.println(Arrays.deepToString(copy));

    ArrayList<StringBuilder> list = new ArrayList<>(List.of(new StringBuilder("a")));
    @SuppressWarnings("unchecked")
    ArrayList<StringBuilder> cloned = (ArrayList<StringBuilder>) list.clone();
    cloned.get(0).append("!");
    System.out.println(list);
  }
}
// ANCHOR_END: clone-edge-demo
