package oop.contracts.solutions;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/** Comparator consistent with equals: tie-break on name so no element is lost. */
public final class SolutionTieBreak {
  record P(String name, int rank) {}

  public static void main(String[] args) {
    Set<P> lossy = new TreeSet<>(Comparator.comparingInt(P::rank));
    Set<P> safe = new TreeSet<>(Comparator.comparingInt(P::rank).thenComparing(P::name));
    for (P p : new P[] {new P("a", 1), new P("b", 1)}) {
      lossy.add(p);
      safe.add(p);
    }
    System.out.println(lossy.size());
    System.out.println(safe.size());
  }
}
