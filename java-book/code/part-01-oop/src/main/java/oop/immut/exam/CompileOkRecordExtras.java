package oop.immut.exam;

// ANCHOR: compile-ok-record-extras
/** Compiles: static members, extra constructor delegating to canonical, interface, methods. */
public final class CompileOkRecordExtras {
  interface Named {
    String name();
  }

  record User(String name, int age) implements Named {
    static int created;

    User(String name) {
      this(name, 0);
    }

    static User guest() {
      created++;
      return new User("guest");
    }

    boolean adult() {
      return age >= 18;
    }
  }

  public static void main(String[] args) {
    User u = User.guest();
    System.out.println(u + " " + u.adult() + " " + User.created);
  }
}
// ANCHOR_END: compile-ok-record-extras
