package patterns.structural.composite.solutions;

import java.util.ArrayList;
import java.util.List;
import patterns.structural.composite.Expr;
import patterns.structural.composite.Expr.Add;
import patterns.structural.composite.Expr.Mul;
import patterns.structural.composite.Expr.Num;

public final class Ch37Solutions {
  // ANCHOR: ch37-ex1
  /** Ex1: menu composite: items and combos priced through one interface. */
  interface MenuPart {
    int price();
  }

  record Item(String name, int price) implements MenuPart {}

  record Combo(String name, List<MenuPart> parts) implements MenuPart {
    @Override
    public int price() {
      return parts.stream().mapToInt(MenuPart::price).sum();
    }
  }
  // ANCHOR_END: ch37-ex1

  // ANCHOR: ch37-ex2
  /** Ex2: depth of an Expr tree (a lone Num has depth 1). */
  static int depth(Expr e) {
    return switch (e) {
      case Num n -> 1;
      case Add a -> 1 + Math.max(depth(a.left()), depth(a.right()));
      case Mul m -> 1 + Math.max(depth(m.left()), depth(m.right()));
    };
  }
  // ANCHOR_END: ch37-ex2

  // ANCHOR: ch37-ex3
  /** Ex3: count Num leaves in an Expr tree. */
  static int leaves(Expr e) {
    return switch (e) {
      case Num n -> 1;
      case Add a -> leaves(a.left()) + leaves(a.right());
      case Mul m -> leaves(m.left()) + leaves(m.right());
    };
  }
  // ANCHOR_END: ch37-ex3

  // ANCHOR: ch37-ex4
  /** Ex4: safe add: reject a child that already contains the parent (no cycles). */
  static final class Group {
    final String name;
    final List<Group> kids = new ArrayList<>();

    Group(String name) {
      this.name = name;
    }

    boolean contains(Group other) {
      if (this == other) {
        return true;
      }
      for (Group k : kids) {
        if (k.contains(other)) {
          return true;
        }
      }
      return false;
    }

    Group add(Group child) {
      if (child.contains(this)) {
        throw new IllegalArgumentException("cycle: " + child.name + " already contains " + name);
      }
      kids.add(child);
      return this;
    }
  }
  // ANCHOR_END: ch37-ex4

  // ANCHOR: ch37-ex5
  /** Ex5: flatten leaf names depth-first. */
  sealed interface Tree permits Leaf, Branch {}

  record Leaf(String name) implements Tree {}

  record Branch(List<Tree> children) implements Tree {}

  static List<String> flatten(Tree t) {
    List<String> out = new ArrayList<>();
    collect(t, out);
    return out;
  }

  private static void collect(Tree t, List<String> out) {
    switch (t) {
      case Leaf l -> out.add(l.name());
      case Branch b -> b.children().forEach(c -> collect(c, out));
    }
  }
  // ANCHOR_END: ch37-ex5

  public static void main(String[] args) {
    Combo meal = new Combo("meal", List.of(new Item("burger", 5), new Item("fries", 2), new Combo("drinks", List.of(new Item("cola", 1)))));
    System.out.println("ex1 " + meal.price());
    Expr e = new Mul(new Add(new Num(1), new Num(2)), new Num(3));
    System.out.println("ex2 " + depth(e));
    System.out.println("ex3 " + leaves(e));
    Group a = new Group("a");
    Group b = new Group("b").add(a);
    try {
      a.add(b);
    } catch (IllegalArgumentException ex) {
      System.out.println("ex4 " + ex.getMessage());
    }
    Tree t = new Branch(List.of(new Leaf("x"), new Branch(List.of(new Leaf("y"), new Leaf("z"))), new Leaf("w")));
    System.out.println("ex5 " + flatten(t));
  }
}
