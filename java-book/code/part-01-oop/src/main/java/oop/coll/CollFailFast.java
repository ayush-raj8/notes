package oop.coll;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

// ANCHOR: coll-fail-fast
public final class CollFailFast {
  static List<String> abc() {
    return new ArrayList<>(List.of("a", "b", "c"));
  }

  public static void main(String[] args) {
    List<String> first = abc();
    try {
      for (String s : first) {
        if (s.equals("a")) {
          first.remove(s); // structural change inside for-each
        }
      }
    } catch (ConcurrentModificationException e) {
      System.out.println("remove \"a\": ConcurrentModificationException");
    }

    List<String> second = abc();
    for (String s : second) {
      if (s.equals("b")) {
        second.remove(s); // second-to-last element: hasNext() is already false
      }
    }
    System.out.println("remove \"b\": no exception, list = " + second);

    List<String> viaIterator = abc();
    for (Iterator<String> it = viaIterator.iterator(); it.hasNext(); ) {
      if (it.next().equals("a")) {
        it.remove(); // the supported way
      }
    }
    System.out.println("Iterator.remove: " + viaIterator);

    List<String> viaRemoveIf = abc();
    viaRemoveIf.removeIf(s -> s.equals("c"));
    System.out.println("removeIf: " + viaRemoveIf);

    List<String> back = abc();
    ListIterator<String> li = back.listIterator(back.size());
    StringBuilder sb = new StringBuilder();
    while (li.hasPrevious()) {
      sb.append(li.previous());
    }
    System.out.println("ListIterator backwards: " + sb);
  }
}
// ANCHOR_END: coll-fail-fast
