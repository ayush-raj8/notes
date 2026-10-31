package oop.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Types used to read class-diagram notation: visibility, static, abstract, interface, generics. */
public final class NotationDemo {
  private NotationDemo() {}

  // ANCHOR: notation-types
  /** <<interface>> Shape */
  interface Shape {
    double area(); // + area() double   (interface members are public abstract)
  }

  /** abstract class Base  (name in italics in UML; Mermaid marks abstract members with *) */
  abstract static class Base implements Shape {
    private static int created; // - created: int $   (underlined = static)
    private final int id; // - id: int
    protected String label; // # label: String
    String tag = "t"; // ~ tag: String      (package-private)

    protected Base(String label) {
      this.label = label;
      this.id = ++created;
    }

    public abstract double area(); // + area() double *

    public int id() { // + id() int
      return id;
    }

    public static int created() { // + created() int $
      return created;
    }
  }

  /** final class Circle: no subclasses */
  static final class Circle extends Base {
    private final double r;

    Circle(double r) {
      super("circle");
      this.r = r;
    }

    @Override
    public double area() {
      return Math.PI * r * r;
    }
  }

  /** <<interface>> Repository~T~  (generic type parameter) */
  interface Repository<T> {
    void save(T item);

    Optional<T> findFirst();
  }

  /** MemoryRepository~T~ realizes Repository~T~ */
  static final class MemoryRepository<T> implements Repository<T> {
    private final List<T> items = new ArrayList<>();

    @Override
    public void save(T item) {
      items.add(item);
    }

    @Override
    public Optional<T> findFirst() {
      return items.stream().findFirst();
    }
  }
  // ANCHOR_END: notation-types

  public static void main(String[] args) {
    Circle c = new Circle(1);
    Circle d = new Circle(2);
    System.out.printf("ids: %d %d, created: %d%n", c.id(), d.id(), Base.created());
    System.out.printf("area: %.2f%n", c.area());
    Repository<String> repo = new MemoryRepository<>();
    System.out.println("empty: " + repo.findFirst());
    repo.save("x");
    System.out.println("first: " + repo.findFirst());
  }
}
