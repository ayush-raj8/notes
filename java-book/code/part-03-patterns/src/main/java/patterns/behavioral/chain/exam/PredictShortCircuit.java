package patterns.behavioral.chain.exam;

import java.util.List;
import patterns.behavioral.chain.Filter;
import patterns.behavioral.chain.FilterChain;
import patterns.behavioral.chain.Request;

// ANCHOR: predict-short-circuit
public final class PredictShortCircuit {
  public static void main(String[] args) {
    Filter a =
        (r, c) -> {
          System.out.print("a ");
          return c.proceed(r);
        };
    Filter stop =
        (r, c) -> {
          System.out.print("stop ");
          return "blocked";
        };
    Filter b =
        (r, c) -> {
          System.out.print("b ");
          return c.proceed(r);
        };
    String res = new FilterChain(List.of(a, stop, b), r -> "terminal").proceed(new Request("u", "/"));
    System.out.println(res);
  }
}
// ANCHOR_END: predict-short-circuit
