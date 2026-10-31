package patterns.behavioral.chain.exam;

import java.util.List;
import patterns.behavioral.chain.Filter;
import patterns.behavioral.chain.FilterChain;
import patterns.behavioral.chain.Request;

// ANCHOR: predict-onion
public final class PredictOnion {
  static Filter wrap(String name) {
    return (r, c) -> {
      System.out.print(name + "-in ");
      String res = c.proceed(r);
      System.out.print(name + "-out ");
      return res;
    };
  }

  public static void main(String[] args) {
    List<Filter> fs = List.of(wrap("x"), wrap("y"), wrap("z"));
    String res = new FilterChain(fs, r -> "core").proceed(new Request("u", "/"));
    System.out.println(res);
  }
}
// ANCHOR_END: predict-onion
