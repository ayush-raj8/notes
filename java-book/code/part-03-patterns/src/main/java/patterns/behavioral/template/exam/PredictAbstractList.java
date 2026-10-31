package patterns.behavioral.template.exam;

import java.util.AbstractList;

// ANCHOR: predict-abstract-list
public final class PredictAbstractList {
  static final class Squares extends AbstractList<Integer> {
    @Override
    public Integer get(int i) {
      return i * i;
    }

    @Override
    public int size() {
      return 4;
    }
  }

  public static void main(String[] args) {
    Squares s = new Squares();
    System.out.println(s + " " + s.contains(9) + " " + s.indexOf(4));
    try {
      s.add(99);
    } catch (UnsupportedOperationException e) {
      System.out.println("add: UnsupportedOperationException");
    }
    try {
      s.set(0, 5);
    } catch (UnsupportedOperationException e) {
      System.out.println("set: UnsupportedOperationException");
    }
    System.out.println(s.size());
  }
}
// ANCHOR_END: predict-abstract-list
