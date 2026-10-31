package oop.nested;

// ANCHOR: interface-nested
/** Types declared inside an interface are implicitly public and static. */
public interface InterfaceNested {
  int LIMIT = 3; // implicitly public static final

  class Config { // implicitly public static
    public final int limit = LIMIT;
  }

  enum Mode {
    FAST,
    SAFE
  }

  record Pair(String a, String b) {}

  interface Callback {
    void done(String msg);
  }

  /** The interface's own abstract method may use its nested types in the signature. */
  Mode mode();

  static void main(String[] args) {
    Config c = new InterfaceNested.Config(); // no enclosing instance: it is static
    System.out.println(c.limit);
    System.out.println(Mode.valueOf("SAFE").ordinal());
    System.out.println(new Pair("x", "y"));
    Callback cb = msg -> System.out.println("cb:" + msg);
    cb.done("ok");
    System.out.println(Config.class.getName());
  }
}
// ANCHOR_END: interface-nested
