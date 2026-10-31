package oop.contracts;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

// ANCHOR: iterator-remove
public final class IteratorRemoveDemo {
  public static void main(String[] args) {
    List<Integer> a = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
    try {
      for (Integer x : a) {
        if (x % 2 == 0) {
          a.remove(x);
        }
      }
    } catch (ConcurrentModificationException e) {
      System.out.println("CME " + a);
    }

    List<Integer> b = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
    Iterator<Integer> it = b.iterator();
    while (it.hasNext()) {
      if (it.next() % 2 == 0) {
        it.remove();
      }
    }
    System.out.println(b);

    List<Integer> c = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
    c.removeIf(x -> x % 2 == 0);
    System.out.println(c);

    try {
      Iterator<Integer> bad = c.iterator();
      bad.remove();
    } catch (IllegalStateException e) {
      System.out.println("IllegalStateException");
    }
  }
}
// ANCHOR_END: iterator-remove
