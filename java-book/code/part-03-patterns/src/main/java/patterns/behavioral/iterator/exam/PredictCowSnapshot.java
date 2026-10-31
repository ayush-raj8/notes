package patterns.behavioral.iterator.exam;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// ANCHOR: predict-cow-snapshot
public final class PredictCowSnapshot {
  public static void main(String[] args) {
    List<String> list = new CopyOnWriteArrayList<>(List.of("a", "b"));
    for (String s : list) {
      System.out.print(s + " ");
      if (s.equals("a")) list.add("late");
    }
    System.out.println("-> " + list);
    Iterator<String> it = list.iterator();
    it.next();
    try {
      it.remove();
    } catch (UnsupportedOperationException e) {
      System.out.println("remove: UnsupportedOperationException");
    }
  }
}
// ANCHOR_END: predict-cow-snapshot
