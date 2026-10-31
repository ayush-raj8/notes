package oop.coll.exam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// ANCHOR: predict-remove-index-vs-object
public final class PredictRemoveIndexVsObject {
  public static void main(String[] args) {
    List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
    list.remove(1);
    System.out.println(list);
    list.remove(Integer.valueOf(1));
    System.out.println(list);

    Set<Short> set = new HashSet<>();
    for (short i = 0; i < 5; i++) {
      set.add(i);
      set.remove(i - 1);
    }
    System.out.println(set.size());
  }
}
// ANCHOR_END: predict-remove-index-vs-object
