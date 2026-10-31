package patterns.creational.abstractfactory.exam;

import patterns.creational.abstractfactory.DarkFactory;
import patterns.creational.abstractfactory.Screen;
import patterns.creational.abstractfactory.UiFactory;

// ANCHOR: predict-family-consistency
public final class PredictFamilyConsistency {
  public static void main(String[] args) {
    UiFactory f = new DarkFactory();
    Screen s = new Screen(f);
    System.out.println(s.draw());
    System.out.println(f.createButton().render().equals(f.createButton().render()));
  }
}
// ANCHOR_END: predict-family-consistency
