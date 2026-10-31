package lld.splitwise.exam;

import java.util.List;
import lld.splitwise.EqualSplit;

// ANCHOR: predict-equal-remainder
public class PredictEqualRemainder {
  public static void main(String[] args) {
    System.out.println(new EqualSplit(List.of("a", "b", "c")).shares(100));
    System.out.println(new EqualSplit(List.of("a", "b", "c", "d")).shares(10));
    System.out.println(new EqualSplit(List.of("a", "b")).shares(7).values().stream().mapToLong(x -> x).sum());
  }
}
// ANCHOR_END: predict-equal-remainder
