package oop.generics.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-pecs-copy
public final class PredictPecsCopy {
  static void copy(List<? super Integer> dst, List<? extends Integer> src) {
    for (Integer i : src) {
      dst.add(i * 2);
    }
  }

  public static void main(String[] args) {
    List<Number> dst = new ArrayList<>(List.of(0.5));
    copy(dst, List.of(1, 2));
    System.out.println(dst);
  }
}
// ANCHOR_END: predict-pecs-copy
