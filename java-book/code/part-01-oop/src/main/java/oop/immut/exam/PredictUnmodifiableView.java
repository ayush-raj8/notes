package oop.immut.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: predict-unmodifiable-view
public final class PredictUnmodifiableView {
  public static void main(String[] args) {
    List<Integer> base = new ArrayList<>(List.of(1));
    List<Integer> view = Collections.unmodifiableList(base);
    List<Integer> copy = List.copyOf(base);
    base.add(2);
    System.out.println(view.size() + " " + copy.size());
    try {
      view.add(3);
    } catch (UnsupportedOperationException e) {
      System.out.println("UOE");
    }
  }
}
// ANCHOR_END: predict-unmodifiable-view
