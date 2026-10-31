package oop.generics.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-raw-pollution
public final class PredictRawPollution {
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void main(String[] args) {
    List<String> xs = new ArrayList<>();
    List raw = xs;
    raw.add(7);
    System.out.println("added");
    System.out.println(xs.size());
    String s = xs.get(0);
    System.out.println("read");
  }
}
// ANCHOR_END: predict-raw-pollution
