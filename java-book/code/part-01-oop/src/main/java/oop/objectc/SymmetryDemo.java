package oop.objectc;

// ANCHOR: symmetry
/** instanceof-based equals across a subclass breaks symmetry; getClass() restores it. */
public final class SymmetryDemo {
  static class Pt {
    final int x;

    Pt(int x) {
      this.x = x;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Pt p && p.x == x;
    }

    @Override
    public int hashCode() {
      return x;
    }
  }

  static final class ColorPt extends Pt {
    final String color;

    ColorPt(int x, String color) {
      super(x);
      this.color = color;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof ColorPt c && c.x == x && c.color.equals(color);
    }

    @Override
    public int hashCode() {
      return 31 * x + color.hashCode();
    }
  }

  static class StrictPt {
    final int x;

    StrictPt(int x) {
      this.x = x;
    }

    @Override
    public boolean equals(Object o) {
      return o != null && o.getClass() == getClass() && ((StrictPt) o).x == x;
    }

    @Override
    public int hashCode() {
      return x;
    }
  }

  static final class StrictColor extends StrictPt {
    StrictColor(int x) {
      super(x);
    }
  }

  public static void main(String[] args) {
    Pt p = new Pt(1);
    ColorPt c = new ColorPt(1, "red");
    System.out.println("p.equals(c)=" + p.equals(c));
    System.out.println("c.equals(p)=" + c.equals(p));

    StrictPt s = new StrictPt(1);
    StrictColor t = new StrictColor(1);
    System.out.println("s.equals(t)=" + s.equals(t));
    System.out.println("t.equals(s)=" + t.equals(s));
  }
}
// ANCHOR_END: symmetry
