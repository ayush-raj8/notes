package oop.abstracts.exam;

import java.util.function.Function;

// ANCHOR: predict-functional-compose
public final class PredictFunctionalCompose {
  public static void main(String[] args) {
    Function<Integer, Integer> plus2 = x -> x + 2;
    Function<Integer, Integer> times3 = x -> x * 3;
    System.out.println(plus2.andThen(times3).apply(1));
    System.out.println(plus2.compose(times3).apply(1));
    System.out.println(Function.<Integer>identity().andThen(plus2).apply(0));
  }
}
// ANCHOR_END: predict-functional-compose
