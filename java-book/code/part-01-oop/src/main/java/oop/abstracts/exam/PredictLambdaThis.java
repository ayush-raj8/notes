package oop.abstracts.exam;

import java.util.function.Supplier;

// ANCHOR: predict-lambda-this
public final class PredictLambdaThis {
  String name = "outer";

  Supplier<String> lam = () -> this.name;

  Supplier<String> anon =
      new Supplier<>() {
        String name = "anon";

        @Override
        public String get() {
          return this.name;
        }
      };

  public static void main(String[] args) {
    PredictLambdaThis o = new PredictLambdaThis();
    System.out.println(o.lam.get() + " " + o.anon.get());
  }
}
// ANCHOR_END: predict-lambda-this
