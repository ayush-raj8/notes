package oop.nested.solutions;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Exercise 1: static nested Node + inner Iterator. */
public final class SolutionLinkedStack<T> implements Iterable<T> {
  private static final class Node<T> {
    final T value;
    final Node<T> next;

    Node(T value, Node<T> next) {
      this.value = value;
      this.next = next;
    }
  }

  private Node<T> top;

  public void push(T v) {
    top = new Node<>(v, top);
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private Node<T> cur = top;

      @Override
      public boolean hasNext() {
        return cur != null;
      }

      @Override
      public T next() {
        if (cur == null) {
          throw new NoSuchElementException();
        }
        T v = cur.value;
        cur = cur.next;
        return v;
      }
    };
  }

  public static void main(String[] args) {
    SolutionLinkedStack<Integer> s = new SolutionLinkedStack<>();
    s.push(1);
    s.push(2);
    s.push(3);
    for (int x : s) {
      System.out.print(x);
    }
    System.out.println();
  }
}
