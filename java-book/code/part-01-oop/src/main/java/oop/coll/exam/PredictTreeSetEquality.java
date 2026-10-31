package oop.coll.exam;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// ANCHOR: predict-tree-set-equality
public final class PredictTreeSetEquality {
  public static void main(String[] args) {
    List<BigDecimal> values = List.of(new BigDecimal("1.0"), new BigDecimal("1.00"));
    Set<BigDecimal> hash = new HashSet<>(values);
    Set<BigDecimal> tree = new TreeSet<>(values);
    System.out.println(hash.size() + " " + tree.size());
    System.out.println(new BigDecimal("1.0").equals(new BigDecimal("1.00")));
    System.out.println(new BigDecimal("1.0").compareTo(new BigDecimal("1.00")));

    TreeSet<String> ci = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    ci.add("Java");
    System.out.println(ci.add("JAVA") + " " + ci);
  }
}
// ANCHOR_END: predict-tree-set-equality
