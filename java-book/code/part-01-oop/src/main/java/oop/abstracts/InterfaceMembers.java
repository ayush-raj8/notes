package oop.abstracts;

// ANCHOR: interface-members
interface Greeter {
  int MAX_NAME = 8; // public static final

  String name(); // public abstract

  default String greet() { // inherited, overridable
    return prefix() + name();
  }

  static Greeter of(String n) { // not inherited by implementors
    return () -> n;
  }

  private String prefix() { // Java 9+: helper, not visible outside
    return "Hi ";
  }
}

class Anna implements Greeter {
  @Override
  public String name() {
    return "Anna";
  }
}

public final class InterfaceMembers {
  public static void main(String[] args) {
    Greeter g = new Anna();
    System.out.println(g.greet());
    System.out.println(Greeter.MAX_NAME);
    System.out.println(Greeter.of("Bo").greet());
    System.out.println(Greeter.class.isInterface());
  }
}
// ANCHOR_END: interface-members
