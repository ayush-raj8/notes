package patterns.creational.abstractfactory.exam;

import patterns.creational.abstractfactory.UiFactories;

// ANCHOR: predict-unknown-theme
public final class PredictUnknownTheme {
  public static void main(String[] args) {
    System.out.println(UiFactories.of("dark").createField().render());
    try {
      System.out.println(UiFactories.of("Dark").createField().render());
    } catch (IllegalArgumentException e) {
      System.out.println("IAE " + e.getMessage());
    }
    System.out.println("done");
  }
}
// ANCHOR_END: predict-unknown-theme
