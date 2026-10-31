package oop.contracts.exam;

import java.util.Iterator;
import java.util.List;

// ANCHOR: predict-iterator
public final class PredictIterator {
  public static void main(String[] args) {
    List<Integer> list = List.of(10, 20, 30);
    Iterator<Integer> it = list.iterator();
    System.out.println(it.hasNext());
    System.out.println(it.hasNext());
    System.out.println(it.next());
    System.out.println(it.next() + it.next());
    System.out.println(it.hasNext());
    try {
      list.iterator().remove();
    } catch (RuntimeException e) {
      System.out.println(e.getClass().getSimpleName());
    }
  }
}
// ANCHOR_END: predict-iterator
