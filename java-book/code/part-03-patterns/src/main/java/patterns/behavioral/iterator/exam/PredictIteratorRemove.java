package patterns.behavioral.iterator.exam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// ANCHOR: predict-iterator-remove
public final class PredictIteratorRemove {
  public static void main(String[] args) {
    List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
    Iterator<Integer> it = list.iterator();
    try {
      it.remove();
    } catch (IllegalStateException e) {
      System.out.println("remove before next: IllegalStateException");
    }
    it.next();
    it.remove();
    try {
      it.remove();
    } catch (IllegalStateException e) {
      System.out.println("remove twice: IllegalStateException");
    }
    it.next();
    it.next();
    System.out.println(it.hasNext() + " " + it.hasNext());
    try {
      it.next();
    } catch (NoSuchElementException e) {
      System.out.println("next past end: NoSuchElementException");
    }
    System.out.println(list);
  }
}
// ANCHOR_END: predict-iterator-remove
