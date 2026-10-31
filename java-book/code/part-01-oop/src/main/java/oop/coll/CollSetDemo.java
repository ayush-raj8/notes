package oop.coll;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// ANCHOR: coll-set
public final class CollSetDemo {
  public static void main(String[] args) {
    List<String> words = List.of("pear", "apple", "pear", "fig", "apple");

    Set<String> hash = new HashSet<>(words);
    System.out.println("HashSet size=" + hash.size() + " (order unspecified)");
    System.out.println("LinkedHashSet: " + new LinkedHashSet<>(words));
    System.out.println("TreeSet      : " + new TreeSet<>(words));

    Set<String> s = new HashSet<>();
    System.out.println("add new: " + s.add("x") + ", add duplicate: " + s.add("x"));

    TreeSet<Integer> t = new TreeSet<>(List.of(10, 20, 30, 40));
    System.out.println("first=" + t.first() + " last=" + t.last());
    System.out.println("floor(25)=" + t.floor(25) + " ceiling(25)=" + t.ceiling(25));
    System.out.println("lower(10)=" + t.lower(10) + " higher(40)=" + t.higher(40));
    System.out.println("headSet(30)=" + t.headSet(30) + " tailSet(30)=" + t.tailSet(30));

    // TreeSet decides "duplicate" with compare()==0, not equals()
    TreeSet<String> ci = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    ci.add("Java");
    System.out.println("add \"JAVA\" to case-insensitive TreeSet: " + ci.add("JAVA") + ", set=" + ci);

    TreeSet<String> byLength = new TreeSet<>(Comparator.comparingInt(String::length));
    byLength.addAll(List.of("aa", "bb", "c"));
    System.out.println("by length (aa, bb tie): " + byLength);

    BigDecimal one = new BigDecimal("1.0");
    BigDecimal onePlus = new BigDecimal("1.00");
    System.out.println("HashSet size=" + new HashSet<>(List.of(one, onePlus)).size()
        + " TreeSet size=" + new TreeSet<>(List.of(one, onePlus)).size());
  }
}
// ANCHOR_END: coll-set
