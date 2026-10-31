package oop.override.solutions;

import java.util.List;
import java.util.Objects;

/** Exercise 2: override equals(Object) and hashCode with @Override. */
public final class SolutionEquals {
  static final class Pt {
    final int x;
    final int y;

    Pt(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Pt p && x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }
  }

  public static void main(String[] args) {
    Object o = new Pt(1, 2);
    System.out.println(new Pt(1, 2).equals(o));
    System.out.println(List.of(new Pt(1, 2)).contains(new Pt(1, 2)));
  }
}
