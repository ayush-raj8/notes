package oop.coll.exam;

import java.util.Arrays;
import java.util.List;

// ANCHOR: predict-as-list
public final class PredictAsList {
  public static void main(String[] args) {
    int[] prim = {1, 2, 3};
    Integer[] boxed = {1, 2, 3};
    System.out.println(Arrays.asList(prim).size());
    System.out.println(Arrays.asList(boxed).size());

    List<Integer> view = Arrays.asList(boxed);
    boxed[0] = 99;
    view.set(1, 77);
    System.out.println(view + " " + Arrays.toString(boxed));
    try {
      view.add(4);
    } catch (UnsupportedOperationException e) {
      System.out.println("UOE");
    }
  }
}
// ANCHOR_END: predict-as-list
