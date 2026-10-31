package oop.immut.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-final-list
public final class PredictFinalList {
  public static void main(String[] args) {
    final List<String> xs = new ArrayList<>();
    xs.add("a");
    xs.add("b");
    System.out.println(xs);
  }
}
// ANCHOR_END: predict-final-list
