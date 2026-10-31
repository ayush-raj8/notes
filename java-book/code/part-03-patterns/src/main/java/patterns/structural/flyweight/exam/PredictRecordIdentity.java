package patterns.structural.flyweight.exam;

import patterns.structural.flyweight.Tree;
import patterns.structural.flyweight.TreeFactory;
import patterns.structural.flyweight.TreeType;

// ANCHOR: predict-record-identity
public final class PredictRecordIdentity {
  public static void main(String[] args) {
    TreeFactory.reset();
    TreeType t = TreeFactory.get("fir", "green");
    Tree a = new Tree(1, 2, t);
    Tree b = new Tree(1, 2, t);
    Tree c = new Tree(1, 2, TreeFactory.get("fir", "blue"));
    System.out.println(a == b);
    System.out.println(a.equals(b));
    System.out.println(a.equals(c));
    System.out.println(a.type() == b.type());
  }
}
// ANCHOR_END: predict-record-identity
