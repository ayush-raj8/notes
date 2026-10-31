package oop.abstracts;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

// ANCHOR: functional-interfaces
@FunctionalInterface
interface Op {
  int apply(int a, int b); // the single abstract method

  boolean equals(Object o); // Object's public methods do not count

  default Op twice() { // default methods do not count
    return (a, b) -> apply(apply(a, b), b);
  }

  static Op add() {
    return (a, b) -> a + b;
  }
}

public final class FunctionalInterfaces {
  static int helper(int x) {
    return x * 10;
  }

  public static void main(String[] args) {
    Op plus = (a, b) -> a + b;
    Op viaRef = Math::max;
    System.out.println(plus.apply(2, 3));
    System.out.println(plus.twice().apply(2, 3));
    System.out.println(viaRef.apply(2, 3));
    System.out.println(Op.add().apply(1, 1));

    Function<Integer, Integer> inc = x -> x + 1;
    Function<Integer, Integer> dbl = x -> x * 2;
    System.out.println(inc.andThen(dbl).apply(5));
    System.out.println(inc.compose(dbl).apply(5));
    Predicate<String> empty = String::isEmpty;
    System.out.println(empty.negate().test("x"));
    Supplier<String> sup = () -> "supplied";
    System.out.println(sup.get());
    BiFunction<String, Integer, String> rep = String::repeat;
    System.out.println(rep.apply("ab", 2));
    UnaryOperator<Integer> viaStatic = FunctionalInterfaces::helper;
    System.out.println(viaStatic.apply(4));
  }
}
// ANCHOR_END: functional-interfaces
