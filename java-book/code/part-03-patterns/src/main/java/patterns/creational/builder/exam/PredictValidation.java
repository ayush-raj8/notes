package patterns.creational.builder.exam;

import patterns.creational.builder.Pizza;

// ANCHOR: predict-validation
public final class PredictValidation {
  public static void main(String[] args) {
    Pizza.Builder b = Pizza.builder("L").cheese().pepperoni();
    System.out.println(b.build().size());
    b.olives();
    try {
      b.build();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    try {
      Pizza.builder(null).build();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-validation
