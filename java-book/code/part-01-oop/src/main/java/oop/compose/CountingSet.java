package oop.compose;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// ANCHOR: counting-set
/** Composition: wraps any {@code Set}; correct whatever the wrapped {@code addAll} does. */
public final class CountingSet<E> extends ForwardingSet<E> {
  private int addCount;

  public CountingSet(Set<E> delegate) {
    super(delegate);
  }

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
    CountingSet<String> s = new CountingSet<>(new HashSet<>());
    s.addAll(List.of("a", "b", "c"));
    System.out.println("addCount=" + s.addCount() + " size=" + s.size());
  }
}
// ANCHOR_END: counting-set
