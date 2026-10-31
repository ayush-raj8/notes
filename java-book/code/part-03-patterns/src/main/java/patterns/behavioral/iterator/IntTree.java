package patterns.behavioral.iterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

// ANCHOR: int-tree
/** Binary search tree whose iterator hides the traversal (in-order, explicit stack). */
public final class IntTree implements Iterable<Integer> {
  private static final class Node {
    final int value;
    Node left;
    Node right;

    Node(int value) {
      this.value = value;
    }
  }

  private Node root;

  public void add(int v) {
    root = insert(root, v);
  }

  private static Node insert(Node n, int v) {
    if (n == null) return new Node(v);
    if (v < n.value) n.left = insert(n.left, v);
    else if (v > n.value) n.right = insert(n.right, v);
    return n;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<>() {
      private final Deque<Node> stack = new ArrayDeque<>();

      {
        pushLeft(root);
      }

      private void pushLeft(Node n) {
        for (; n != null; n = n.left) stack.push(n);
      }

      @Override
      public boolean hasNext() {
        return !stack.isEmpty();
      }

      @Override
      public Integer next() {
        if (stack.isEmpty()) throw new NoSuchElementException();
        Node n = stack.pop();
        pushLeft(n.right);
        return n.value;
      }
    };
  }
}
// ANCHOR_END: int-tree
