package solutions.ch45;

import java.util.List;
import java.util.function.UnaryOperator;

/** Exercise 5: a pipeline chain — every link transforms the value (no short-circuit). */
public final class SolutionFunctionCompose {
  public static void main(String[] args) {
    List<UnaryOperator<String>> links = List.of(String::trim, String::toLowerCase, s -> s.replace(' ', '-'));
    String value = "  Hello Big World ";
    for (UnaryOperator<String> link : links) {
      value = link.apply(value);
    }
    System.out.println("[" + value + "]");
  }
}
