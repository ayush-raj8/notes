package patterns.structural.decorator.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: predict-unmodifiable-view
public final class PredictUnmodifiableView {
  public static void main(String[] args) {
    List<Integer> base = new ArrayList<>(List.of(1, 2));
    List<Integer> view = Collections.unmodifiableList(base);
    List<Integer> copy = List.copyOf(base);
    base.add(3);
    System.out.println(view);
    System.out.println(copy);
    try {
      view.set(0, 9);
    } catch (UnsupportedOperationException e) {
      System.out.println("UOE");
    }
    System.out.println(base);
  }
}
// ANCHOR_END: predict-unmodifiable-view
