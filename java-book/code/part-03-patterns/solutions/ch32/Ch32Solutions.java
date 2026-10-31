package patterns.creational.prototype.solutions;

import java.util.ArrayList;
import java.util.List;
import patterns.creational.prototype.Shape;
import patterns.creational.prototype.ShapeRegistry;

public final class Ch32Solutions {
  // ANCHOR: ch32-ex1
  /** Ex1: copy constructor performing a deep copy of a mutable list. */
  static final class Playlist {
    private final List<String> songs;

    Playlist(List<String> songs) {
      this.songs = new ArrayList<>(songs);
    }

    Playlist(Playlist other) {
      this(other.songs);
    }

    void add(String s) {
      songs.add(s);
    }

    @Override
    public String toString() {
      return songs.toString();
    }
  }
  // ANCHOR_END: ch32-ex1

  // ANCHOR: ch32-ex2
  /** Ex2: a new prototype type without editing the registry. */
  static final class Triangle extends Shape {
    private final int side;

    Triangle(String label, int side) {
      super(label);
      this.side = side;
    }

    private Triangle(Triangle o) {
      super(o);
      this.side = o.side;
    }

    @Override
    public Triangle copy() {
      return new Triangle(this);
    }

    @Override
    public String toString() {
      return "Triangle(" + label() + ", " + side + ")";
    }
  }
  // ANCHOR_END: ch32-ex2

  // ANCHOR: ch32-ex3
  /** Ex3: clone() with a covariant return and a deep-copied array field. */
  static final class Board implements Cloneable {
    private int[] cells = {0, 0, 0};

    @Override
    public Board clone() {
      try {
        Board b = (Board) super.clone();
        b.cells = cells.clone(); // deep enough: primitives
        return b;
      } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);
      }
    }

    void set(int i, int v) {
      cells[i] = v;
    }

    @Override
    public String toString() {
      return java.util.Arrays.toString(cells);
    }
  }
  // ANCHOR_END: ch32-ex3

  // ANCHOR: ch32-ex4
  /** Ex4: shallow vs snapshot copy of a list of mutable elements. */
  static String shallowVsSnapshot() {
    List<StringBuilder> src = List.of(new StringBuilder("a"));
    List<StringBuilder> shallow = new ArrayList<>(src);
    shallow.get(0).append("!");
    return src.get(0).toString();
  }
  // ANCHOR_END: ch32-ex4

  // ANCHOR: ch32-ex5
  /** Ex5: registry copies are independent of the stored prototype. */
  static String registryIndependence() {
    ShapeRegistry r = new ShapeRegistry();
    r.register("t", new Triangle("base", 3));
    r.create("t").relabel("tmp");
    return r.create("t").label();
  }
  // ANCHOR_END: ch32-ex5

  public static void main(String[] args) {
    Playlist p = new Playlist(List.of("one"));
    Playlist q = new Playlist(p);
    q.add("two");
    System.out.println("ex1 " + p + " " + q);
    System.out.println("ex2 " + new Triangle("t", 3).copy());
    Board b = new Board();
    Board c = b.clone();
    c.set(0, 5);
    System.out.println("ex3 " + b + " " + c);
    System.out.println("ex4 " + shallowVsSnapshot());
    System.out.println("ex5 " + registryIndependence());
  }
}
