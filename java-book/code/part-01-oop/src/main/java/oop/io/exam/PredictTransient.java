package oop.io.exam;

import java.io.Serializable;
import oop.io.SerialBasics;

// ANCHOR: predict-transient
public final class PredictTransient {
  static class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    static int shared = 1;
    int id = 10;
    transient int cache = 20;
    String label = "x";
  }

  public static void main(String[] args) throws Exception {
    Item item = new Item();
    item.id = 11;
    item.cache = 21;
    item.label = "y";
    Item.shared = 2;
    Item copy = SerialBasics.roundTrip(item);
    System.out.println(copy.id + " " + copy.cache + " " + copy.label + " " + Item.shared);
  }
}
// ANCHOR_END: predict-transient
