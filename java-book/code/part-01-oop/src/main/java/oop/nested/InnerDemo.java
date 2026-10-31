package oop.nested;

// ANCHOR: inner-demo
/** An inner (member) class is bound to an enclosing instance. */
public final class InnerDemo {
  private final String name;
  private int hits;

  public InnerDemo(String name) {
    this.name = name;
  }

  public class Ticket {
    private final int id;

    Ticket() {
      this.id = ++hits; // reads and writes the outer instance field directly
    }

    @Override
    public String toString() {
      return name + "#" + id;
    }
  }

  public Ticket issue() {
    return new Ticket(); // implicit this.new Ticket()
  }

  public static void main(String[] args) {
    InnerDemo a = new InnerDemo("A");
    InnerDemo b = new InnerDemo("B");
    System.out.println(a.issue());
    System.out.println(a.issue());
    System.out.println(b.issue());
    InnerDemo.Ticket t = a.new Ticket(); // outer.new Inner()
    System.out.println(t);
  }
}
// ANCHOR_END: inner-demo
