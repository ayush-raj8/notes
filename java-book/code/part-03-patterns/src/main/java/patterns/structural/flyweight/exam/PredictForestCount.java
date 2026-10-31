package patterns.structural.flyweight.exam;

import patterns.structural.flyweight.Forest;
import patterns.structural.flyweight.TreeFactory;

// ANCHOR: predict-forest-count
public final class PredictForestCount {
  public static void main(String[] args) {
    TreeFactory.reset();
    Forest f = new Forest();
    for (int i = 0; i < 100; i++) {
      f.plant(i, i, "elm", "green");
    }
    f.plant(0, 0, "Elm", "green");
    f.plant(0, 0, "elm", "green");
    System.out.println(f.size());
    System.out.println(TreeFactory.created());
    System.out.println(f.drawAll().get(100));
  }
}
// ANCHOR_END: predict-forest-count
