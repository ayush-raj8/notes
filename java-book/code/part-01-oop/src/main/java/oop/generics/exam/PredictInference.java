package oop.generics.exam;

import java.util.Arrays;
import java.util.List;

// ANCHOR: predict-inference
public final class PredictInference {
  public static void main(String[] args) {
    List<? extends Number> xs = Arrays.asList(1, 2.5);
    System.out.println(xs.get(0).getClass().getSimpleName() + " " + xs.get(1).getClass().getSimpleName());
    List<Object> objs = Arrays.asList("s", 1);
    System.out.println(objs.size());
  }
}
// ANCHOR_END: predict-inference
