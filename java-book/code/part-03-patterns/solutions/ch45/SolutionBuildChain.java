package solutions.ch45;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Exercise 2: build a chain from a list; the first handler that returns a value wins. */
public final class SolutionBuildChain {
  static <T, R> Function<T, Optional<R>> chain(List<Function<T, Optional<R>>> handlers) {
    return t -> {
      for (Function<T, Optional<R>> h : handlers) {
        Optional<R> r = h.apply(t);
        if (r.isPresent()) return r;
      }
      return Optional.empty();
    };
  }

  public static void main(String[] args) {
    Function<Integer, Optional<String>> small = n -> n < 10 ? Optional.of("small") : Optional.empty();
    Function<Integer, Optional<String>> even = n -> n % 2 == 0 ? Optional.of("even") : Optional.empty();
    var c = chain(List.of(small, even));
    System.out.println(c.apply(3));
    System.out.println(c.apply(20));
    System.out.println(c.apply(21));
  }
}
