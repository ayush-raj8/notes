package patterns.behavioral.strategy.solutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public final class Ch40Solutions {
  // ANCHOR: ch40-ex1
  /** Ex1: text formatter strategies as lambdas selected by name. */
  interface Formatter {
    String format(String s);
  }

  static final Map<String, Formatter> FORMATTERS =
      Map.of("upper", String::toUpperCase, "trim", String::trim, "brackets", s -> "[" + s + "]");

  static String apply(String name, String text) {
    Formatter f = FORMATTERS.get(name);
    if (f == null) {
      throw new IllegalArgumentException("unknown formatter: " + name);
    }
    return f.format(text);
  }
  // ANCHOR_END: ch40-ex1

  // ANCHOR: ch40-ex2
  /** Ex2: shipping cost strategy; the context never contains an if-chain on the kind. */
  interface Shipping {
    int cost(int grams);
  }

  record Order(int grams, Shipping shipping) {
    int shippingCost() {
      return shipping.cost(grams);
    }
  }

  static final Shipping STANDARD = g -> 50 + g / 100;
  static final Shipping EXPRESS = g -> 200 + g / 50;
  // ANCHOR_END: ch40-ex2

  // ANCHOR: ch40-ex3
  /** Ex3: sort people by age descending, then name ascending. */
  record Person(String name, int age) {}

  static List<Person> sorted(List<Person> in) {
    List<Person> out = new ArrayList<>(in);
    out.sort(Comparator.comparingInt(Person::age).reversed().thenComparing(Person::name));
    return out;
  }
  // ANCHOR_END: ch40-ex3

  // ANCHOR: ch40-ex4
  /** Ex4: reduce with a BinaryOperator strategy. */
  static int fold(List<Integer> xs, int identity, BinaryOperator<Integer> op) {
    int acc = identity;
    for (int x : xs) {
      acc = op.apply(acc, x);
    }
    return acc;
  }
  // ANCHOR_END: ch40-ex4

  // ANCHOR: ch40-ex5
  /** Ex5: enum with a constant-specific body and a lookup by symbol. */
  enum Cmp {
    LT("<") {
      @Override
      boolean test(int a, int b) {
        return a < b;
      }
    },
    GE(">=") {
      @Override
      boolean test(int a, int b) {
        return a >= b;
      }
    };

    final String symbol;

    Cmp(String symbol) {
      this.symbol = symbol;
    }

    abstract boolean test(int a, int b);

    static Cmp of(String symbol) {
      for (Cmp c : values()) {
        if (c.symbol.equals(symbol)) {
          return c;
        }
      }
      throw new IllegalArgumentException("no operator " + symbol);
    }
  }
  // ANCHOR_END: ch40-ex5

  public static void main(String[] args) {
    System.out.println("ex1 " + apply("brackets", apply("trim", "  hi ")) + " " + apply("upper", "ok"));
    System.out.println("ex2 " + new Order(1000, STANDARD).shippingCost() + " " + new Order(1000, EXPRESS).shippingCost());
    System.out.println("ex3 " + sorted(List.of(new Person("bo", 30), new Person("al", 30), new Person("cy", 41))));
    System.out.println("ex4 " + fold(List.of(1, 2, 3, 4), 0, Integer::sum) + " " + fold(List.of(1, 2, 3, 4), 1, (a, b) -> a * b));
    System.out.println("ex5 " + Cmp.of("<").test(1, 2) + " " + Cmp.of(">=").test(1, 2));
  }
}
