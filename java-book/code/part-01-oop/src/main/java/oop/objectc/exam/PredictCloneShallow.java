package oop.objectc.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-clone-shallow
public final class PredictCloneShallow {
  static final class Box implements Cloneable {
    List<String> items = new ArrayList<>();

    @Override
    public Box clone() {
      try {
        return (Box) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);
      }
    }
  }

  public static void main(String[] args) {
    Box a = new Box();
    Box b = a.clone();
    a.items.add("x");
    System.out.println(b.items.size() + " " + (a.items == b.items) + " " + (a == b));
  }
}
// ANCHOR_END: predict-clone-shallow
