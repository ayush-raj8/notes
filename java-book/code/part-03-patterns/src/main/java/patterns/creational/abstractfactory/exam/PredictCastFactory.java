package patterns.creational.abstractfactory.exam;

import patterns.creational.abstractfactory.DarkFactory;
import patterns.creational.abstractfactory.UiFactories;
import patterns.creational.abstractfactory.UiFactory;

// ANCHOR: predict-cast-factory
public final class PredictCastFactory {
  public static void main(String[] args) {
    UiFactory f = UiFactories.of("dark");
    System.out.println(f instanceof DarkFactory);
    System.out.println(((DarkFactory) f).contrast());
    UiFactory g = UiFactories.of("light");
    try {
      System.out.println(((DarkFactory) g).contrast());
    } catch (ClassCastException e) {
      System.out.println("CCE");
    }
  }
}
// ANCHOR_END: predict-cast-factory
