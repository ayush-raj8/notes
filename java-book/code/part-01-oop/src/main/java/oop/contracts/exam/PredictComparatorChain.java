package oop.contracts.exam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// ANCHOR: predict-comparator-chain
public final class PredictComparatorChain {
  public static void main(String[] args) {
    List<String> list = new ArrayList<>(List.of("bb", "a", "ccc", "aa", "b"));
    list.sort(Comparator.comparing(String::length).reversed().thenComparing(Comparator.naturalOrder()));
    System.out.println(list);
  }
}
// ANCHOR_END: predict-comparator-chain
