package patterns.creational.abstractfactory.exam;

import patterns.creational.abstractfactory.DarkFactory;
import patterns.creational.abstractfactory.LightFactory;
import patterns.creational.abstractfactory.UiFactory;

// ANCHOR: predict-mixed-family
public final class PredictMixedFamily {
  static String pair(UiFactory buttons, UiFactory fields) {
    return buttons.createButton().render() + "|" + fields.createField().render();
  }

  public static void main(String[] args) {
    System.out.println(pair(new LightFactory(), new DarkFactory()));
    System.out.println(pair(new DarkFactory(), new DarkFactory()));
  }
}
// ANCHOR_END: predict-mixed-family
