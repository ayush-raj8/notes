package oop.compose;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

// ANCHOR: counting-hashset
/** Inheritance on a JDK class: depends on {@code HashSet.addAll} calling {@code add}. */
public final class CountingHashSet<E> extends HashSet<E> {
  private int addCount;

  @Override
  public boolean add(E e) {
    addCount++;
    return super.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c);
  }

  public int addCount() {
    return addCount;
  }

  public static void main(String[] args) {
    CountingHashSet<String> s = new CountingHashSet<>();
    s.addAll(List.of("a", "b", "c"));
    System.out.println("addCount=" + s.addCount() + " size=" + s.size());
  }
}
// ANCHOR_END: counting-hashset
