package oop.generics.solutions;

/** Exercise 4: generic pair with a generic static factory. */
public final class SolutionPair {
  record Pair<A, B>(A first, B second) {
    static <X, Y> Pair<X, Y> of(X x, Y y) {
      return new Pair<>(x, y);
    }
  }

  public static void main(String[] args) {
    Pair<String, Integer> p = Pair.of("n", 1);
    System.out.println(p.first() + p.second());
  }
}
