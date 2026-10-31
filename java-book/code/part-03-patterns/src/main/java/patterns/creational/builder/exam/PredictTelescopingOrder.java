package patterns.creational.builder.exam;

import patterns.creational.builder.TelescopingPizza;

// ANCHOR: predict-telescoping-order
public final class PredictTelescopingOrder {
  public static void main(String[] args) {
    System.out.println(new TelescopingPizza("S"));
    System.out.println(new TelescopingPizza("S", true));
    System.out.println(new TelescopingPizza("S", false, true));
    System.out.println(new TelescopingPizza("S", true, false, true));
  }
}
// ANCHOR_END: predict-telescoping-order
