package oop.nested;

import java.util.Iterator;
import java.util.NoSuchElementException;

// ANCHOR: which-kind
/** One small container using each kind where it fits best. */
public final class WhichKind implements Iterable<String> {
  /** static nested: a Node does not need the container. */
  private static final class Node {
    final String value;
    Node next;

    Node(String value) {
      this.value = value;
    }
  }

  private Node head;
  private int size;

  public void add(String s) {
    Node n = new Node(s);
    n.next = head;
    head = n;
    size++;
  }

  /** inner: an iterator is meaningless without its list. */
  private final class It implements Iterator<String> {
    private Node cur = head;

    @Override
    public boolean hasNext() {
      return cur != null;
    }

    @Override
    public String next() {
      if (cur == null) {
        throw new NoSuchElementException();
      }
      String v = cur.value;
      cur = cur.next;
      return v;
    }
  }

  @Override
  public Iterator<String> iterator() {
    return new It();
  }

  /** local class: needs the parameter, used in one method only. */
  public String joinWith(String sep) {
    class Joiner {
      String run() {
        StringBuilder sb = new StringBuilder();
        for (String s : WhichKind.this) {
          if (sb.length() > 0) {
            sb.append(sep);
          }
          sb.append(s);
        }
        return sb.toString();
      }
    }
    return new Joiner().run();
  }

  /** anonymous: a single-use Iterator-like callback. */
  public Runnable printer() {
    return new Runnable() {
      @Override
      public void run() {
        System.out.println("size=" + size);
      }
    };
  }

  public static void main(String[] args) {
    WhichKind k = new WhichKind();
    k.add("a");
    k.add("b");
    k.add("c");
    for (String s : k) {
      System.out.print(s);
    }
    System.out.println();
    System.out.println(k.joinWith("-"));
    k.printer().run();
  }
}
// ANCHOR_END: which-kind
