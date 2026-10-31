package oop.contracts.solutions;

import java.util.Iterator;
import java.util.List;

/** Iterator wrapper that adds peek() without consuming the element. */
public final class SolutionPeekingIterator<T> implements Iterator<T> {
  private final Iterator<T> src;
  private T buffered;
  private boolean has;

  public SolutionPeekingIterator(Iterator<T> src) {
    this.src = src;
  }

  public T peek() {
    if (!has) {
      buffered = src.next();
      has = true;
    }
    return buffered;
  }

  @Override
  public boolean hasNext() {
    return has || src.hasNext();
  }

  @Override
  public T next() {
    if (has) {
      has = false;
      T t = buffered;
      buffered = null;
      return t;
    }
    return src.next();
  }

  public static void main(String[] args) {
    SolutionPeekingIterator<String> it = new SolutionPeekingIterator<>(List.of("x", "y").iterator());
    System.out.println(it.peek());
    System.out.println(it.peek());
    System.out.println(it.next());
    System.out.println(it.next());
    System.out.println(it.hasNext());
  }
}
