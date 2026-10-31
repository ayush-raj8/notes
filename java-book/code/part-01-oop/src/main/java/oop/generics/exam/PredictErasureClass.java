package oop.generics.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-erasure-class
public final class PredictErasureClass {
  public static void main(String[] args) {
    List<String> a = new ArrayList<>();
    List<Integer> b = new ArrayList<>();
    System.out.println(a.getClass() == b.getClass());
    System.out.println(a.getClass().getSimpleName());
  }
}
// ANCHOR_END: predict-erasure-class
