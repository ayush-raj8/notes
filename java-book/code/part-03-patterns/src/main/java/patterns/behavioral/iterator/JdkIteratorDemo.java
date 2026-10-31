package patterns.behavioral.iterator;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

/** JDK iterators: fail-fast {@code ArrayList}, {@code ListIterator}, map entries, {@code Scanner}. */
public final class JdkIteratorDemo {
  // ANCHOR: jdk-demo
  public static void main(String[] args) {
    List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));
    Iterator<String> it = list.iterator();
    while (it.hasNext()) {
      if (it.next().equals("b")) it.remove();
    }
    System.out.println("after Iterator.remove: " + list);

    try {
      for (String s : list) {
        if (s.equals("a")) list.remove(s);
      }
    } catch (ConcurrentModificationException e) {
      System.out.println("list.remove inside for-each: ConcurrentModificationException");
    }

    ListIterator<String> li = list.listIterator();
    while (li.hasNext()) {
      String s = li.next();
      if (s.equals("c")) li.add("c2");
    }
    StringBuilder back = new StringBuilder();
    while (li.hasPrevious()) back.append(li.previous());
    System.out.println("backwards: " + back);

    Map<String, Integer> m = new LinkedHashMap<>(Map.of("x", 1));
    m.put("y", 2);
    m.put("z", 3);
    m.entrySet().removeIf(e -> e.getValue() == 2);
    System.out.println("map: " + m);

    Scanner sc = new Scanner("one two  three");
    Iterator<String> words = sc;
    StringBuilder out = new StringBuilder();
    while (words.hasNext()) out.append(words.next()).append('|');
    System.out.println("scanner: " + out);
  }
  // ANCHOR_END: jdk-demo
}
