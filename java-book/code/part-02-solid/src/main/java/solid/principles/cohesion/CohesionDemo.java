package solid.principles.cohesion;

import java.util.Collection;
import java.util.List;

public final class CohesionDemo {
  // ANCHOR: cohesion-before
  /** Two unrelated field groups. greet/mailTo never touch paging; offset/hasNext never touch contact. */
  public static final class Mixed {
    private final String name;
    private final String email;
    private final int pageSize;
    private final int page;

    public Mixed(String name, String email, int pageSize, int page) {
      this.name = name;
      this.email = email;
      this.pageSize = pageSize;
      this.page = page;
    }

    public String greet() {
      return "Hello, " + name; // name
    }

    public String mailTo() {
      return name + " <" + email + ">"; // name, email
    }

    public int offset() {
      return pageSize * page; // pageSize, page
    }

    public boolean hasNext(int total) {
      return offset() + pageSize < total; // pageSize, page
    }
  }
  // ANCHOR_END: cohesion-before

  // ANCHOR: cohesion-after
  /** Every method uses the fields that define the class. */
  public record Contact(String name, String email) {
    public String greet() {
      return "Hello, " + name;
    }

    public String mailTo() {
      return name + " <" + email + ">";
    }
  }

  public record Pager(int pageSize, int page) {
    public int offset() {
      return pageSize * page;
    }

    public boolean hasNext(int total) {
      return offset() + pageSize < total;
    }
  }
  // ANCHOR_END: cohesion-after

  // ANCHOR: coupling
  /** Coupled to one concrete collection type. */
  public static int sumConcrete(java.util.ArrayList<Integer> xs) {
    int s = 0;
    for (int x : xs) {
      s += x;
    }
    return s;
  }

  /** Coupled only to what it uses: something it can iterate. */
  public static int sum(Collection<Integer> xs) {
    int s = 0;
    for (int x : xs) {
      s += x;
    }
    return s;
  }
  // ANCHOR_END: coupling

  public static void main(String[] args) {
    // ANCHOR: cohesion-main
    Mixed m = new Mixed("Asha", "a@x.org", 10, 2);
    System.out.println(m.greet() + " | " + m.mailTo() + " | offset=" + m.offset());
    Contact c = new Contact("Asha", "a@x.org");
    Pager p = new Pager(10, 2);
    System.out.println(c.greet() + " | " + c.mailTo() + " | offset=" + p.offset() + " next=" + p.hasNext(25));
    System.out.println("sum=" + sum(List.of(1, 2, 3)) + " " + sum(new java.util.HashSet<>(List.of(4, 5))));
    // ANCHOR_END: cohesion-main
  }
}
