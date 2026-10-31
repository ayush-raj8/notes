package patterns.behavioral.template;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** Template Method via composition: the fixed skeleton takes its steps as functions. */
public final class LambdaTemplate {
  // ANCHOR: lambda-template
  static <I, O> String run(
      Supplier<I> read, Predicate<I> valid, Function<I, O> transform, String label) {
    I in = read.get();
    if (!valid.test(in)) {
      return label + ": rejected " + in;
    }
    return label + ": " + transform.apply(in);
  }
  // ANCHOR_END: lambda-template

  public static void main(String[] args) {
    System.out.println(run(() -> "42", s -> !s.isEmpty(), Integer::parseInt, "parse"));
    System.out.println(run(() -> "", s -> !s.isEmpty(), Integer::parseInt, "parse"));
    System.out.println(run(() -> "abc", s -> s.length() == 3, String::toUpperCase, "upper"));
  }
}
