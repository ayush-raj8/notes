package patterns.creational.builder.exam;

import patterns.creational.builder.Pizza;

// ANCHOR: predict-fluent-chain
public final class PredictFluentChain {
  public static void main(String[] args) {
    Pizza.Builder b = Pizza.builder("M");
    Pizza.Builder same = b.cheese();
    System.out.println(b == same);
    Pizza p = same.pepperoni().build();
    System.out.println(p);
    System.out.println(p.size());
  }
}
// ANCHOR_END: predict-fluent-chain
