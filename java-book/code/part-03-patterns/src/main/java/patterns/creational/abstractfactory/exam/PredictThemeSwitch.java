package patterns.creational.abstractfactory.exam;

import patterns.creational.abstractfactory.Screen;
import patterns.creational.abstractfactory.UiFactories;
import patterns.creational.abstractfactory.UiFactory;

// ANCHOR: predict-theme-switch
public final class PredictThemeSwitch {
  public static void main(String[] args) {
    UiFactory f = UiFactories.of("light");
    Screen first = new Screen(f);
    f = UiFactories.of("dark");
    Screen second = new Screen(f);
    System.out.println(first.draw());
    System.out.println(second.draw());
  }
}
// ANCHOR_END: predict-theme-switch
