package patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: transparent-composite
/** Transparent variant: add() lives on the component, so a leaf must reject it at run time. */
public final class TransparentComposite {
  public abstract static class Node {
    public abstract int count();

    public void add(Node child) {
      throw new UnsupportedOperationException("leaf cannot have children");
    }
  }

  public static final class Leaf extends Node {
    @Override
    public int count() {
      return 1;
    }
  }

  public static final class Group extends Node {
    private final List<Node> kids = new ArrayList<>();

    @Override
    public void add(Node child) {
      kids.add(child);
    }

    @Override
    public int count() {
      return kids.stream().mapToInt(Node::count).sum();
    }
  }

  public static void main(String[] args) {
    Group g = new Group();
    g.add(new Leaf());
    g.add(new Leaf());
    System.out.println(g.count());
    try {
      new Leaf().add(new Leaf());
    } catch (UnsupportedOperationException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: transparent-composite
