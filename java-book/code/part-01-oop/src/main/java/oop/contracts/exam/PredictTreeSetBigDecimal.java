package oop.contracts.exam;

import java.util.Set;
import java.util.TreeSet;

// ANCHOR: predict-treeset
public final class PredictTreeSetBigDecimal {
  record P(String name, int rank) implements Comparable<P> {
    public int compareTo(P o) {
      return Integer.compare(rank, o.rank);
    }
  }

  public static void main(String[] args) {
    Set<P> set = new TreeSet<>();
    set.add(new P("a", 1));
    set.add(new P("b", 1));
    set.add(new P("c", 0));
    System.out.println(set.size());
    System.out.println(set);
    System.out.println(set.contains(new P("zzz", 1)));
  }
}
// ANCHOR_END: predict-treeset
