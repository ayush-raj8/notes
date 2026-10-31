package oop.contracts;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

// ANCHOR: compare-equals
public final class CompareToEqualsGotcha {
  public static void main(String[] args) {
    BigDecimal a = new BigDecimal("1.0");
    BigDecimal b = new BigDecimal("1.00");
    System.out.println(a.equals(b));
    System.out.println(a.compareTo(b));

    Set<BigDecimal> hash = new HashSet<>();
    hash.add(a);
    hash.add(b);
    Set<BigDecimal> tree = new TreeSet<>();
    tree.add(a);
    tree.add(b);
    System.out.println(hash.size());
    System.out.println(tree.size());
  }
}
// ANCHOR_END: compare-equals
