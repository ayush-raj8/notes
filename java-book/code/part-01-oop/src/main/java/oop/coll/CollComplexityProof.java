package oop.coll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

// ANCHOR: coll-complexity-proof
/** Counts calls instead of timing them: counts are the same on every machine. */
class CollProbe {
  static int equalsCalls;
  static int hashCalls;
  final int id;
  final boolean badHash;

  CollProbe(int id, boolean badHash) {
    this.id = id;
    this.badHash = badHash;
  }

  @Override
  public boolean equals(Object o) {
    equalsCalls++;
    return o instanceof CollProbe p && p.id == id;
  }

  @Override
  public int hashCode() {
    hashCalls++;
    return badHash ? 7 : id;
  }

  static void reset() {
    equalsCalls = 0;
    hashCalls = 0;
  }
}

public final class CollComplexityProof {
  public static void main(String[] args) {
    int n = 1000;

    List<CollProbe> list = new ArrayList<>();
    Set<CollProbe> set = new HashSet<>();
    for (int i = 0; i < n; i++) {
      list.add(new CollProbe(i, false));
      set.add(new CollProbe(i, false));
    }
    CollProbe.reset();
    list.contains(new CollProbe(n - 1, false));
    System.out.println("ArrayList.contains(last): equals calls = " + CollProbe.equalsCalls + "  (O(n))");
    CollProbe.reset();
    set.contains(new CollProbe(n - 1, false));
    System.out.println("HashSet.contains(last)  : equals calls = " + CollProbe.equalsCalls
        + ", hashCode calls = " + CollProbe.hashCalls + "  (O(1) average)");

    Map<CollProbe, Integer> collide = new HashMap<>();
    for (int i = 0; i < n; i++) {
      collide.put(new CollProbe(i, true), i); // every key has hashCode 7
    }
    CollProbe.reset();
    for (int i = 0; i < n; i++) {
      collide.get(new CollProbe(i, true));
    }
    System.out.println("same hashCode for all keys: " + n + " gets cost more than " + n
        + " equals calls: " + (CollProbe.equalsCalls > n) + "  (degrades toward O(n))");

    int[] compares = {0};
    TreeSet<Integer> tree = new TreeSet<>((a, b) -> {
      compares[0]++;
      return Integer.compare(a, b);
    });
    List<Integer> keys = new ArrayList<>();
    for (int i = 0; i < 1024; i++) {
      keys.add(i);
    }
    Collections.shuffle(keys, new Random(42));
    tree.addAll(keys);
    System.out.println("TreeSet: 1024 inserts used <= 1024 * 20 comparisons: " + (compares[0] <= 1024 * 20)
        + "  (O(log n) each)");

    List<Integer> sorted = new ArrayList<>(tree);
    int[] searchCompares = {0};
    Collections.binarySearch(sorted, 700, (a, b) -> {
      searchCompares[0]++;
      return Integer.compare(a, b);
    });
    System.out.println("binarySearch in 1024 sorted items: <= 11 comparisons: " + (searchCompares[0] <= 11));
  }
}
// ANCHOR_END: coll-complexity-proof
