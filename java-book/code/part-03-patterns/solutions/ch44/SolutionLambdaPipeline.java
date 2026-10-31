package solutions.ch44;

import java.util.function.Consumer;
import java.util.function.Function;

/** Exercise 3: the same idea as a template method that takes its hooks as lambdas. */
public final class SolutionLambdaPipeline {
  static <T, R> void process(T input, Function<T, R> step, Consumer<R> sink) {
    System.out.println("start");
    R result = step.apply(input);
    sink.accept(result);
    System.out.println("stop");
  }

  public static void main(String[] args) {
    process("  trim me ", String::trim, s -> System.out.println("[" + s + "]"));
    process(21, n -> n * 2, n -> System.out.println("n=" + n));
  }
}
