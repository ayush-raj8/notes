package patterns.structural.adapter.exam;

import java.util.Arrays;
import java.util.List;

// ANCHOR: predict-as-list-write-through
public final class PredictAsListWriteThrough {
  public static void main(String[] args) {
    Integer[] arr = {3, 1, 2};
    List<Integer> view = Arrays.asList(arr);
    view.set(0, 9);
    arr[1] = 8;
    System.out.println(view);
    System.out.println(Arrays.toString(arr));
    try {
      view.remove(0);
    } catch (UnsupportedOperationException e) {
      System.out.println("UOE");
    }
    int[] prim = {1, 2};
    System.out.println(Arrays.asList(prim).size());
  }
}
// ANCHOR_END: predict-as-list-write-through
