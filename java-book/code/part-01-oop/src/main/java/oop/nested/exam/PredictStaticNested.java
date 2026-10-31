package oop.nested.exam;

// ANCHOR: predict-static-nested
public final class PredictStaticNested {
  private static int made;

  static class Item {
    final int id;

    Item() {
      id = ++made;
    }
  }

  public static void main(String[] args) {
    new Item();
    new Item();
    Item last = new Item();
    System.out.println(last.id + " " + made);
  }
}
// ANCHOR_END: predict-static-nested
