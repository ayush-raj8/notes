package patterns.structural.flyweight.exam;

import patterns.structural.flyweight.TreeFactory;
import patterns.structural.flyweight.TreeType;

// ANCHOR: predict-shared-types
public final class PredictSharedTypes {
  public static void main(String[] args) {
    TreeFactory.reset();
    TreeType a = TreeFactory.get("oak", "green");
    TreeType b = TreeFactory.get("oak", "green");
    TreeType c = TreeFactory.get("oak", "brown");
    System.out.println(a == b);
    System.out.println(a == c);
    System.out.println(a.equals(b));
    System.out.println(TreeFactory.created());
  }
}
// ANCHOR_END: predict-shared-types
