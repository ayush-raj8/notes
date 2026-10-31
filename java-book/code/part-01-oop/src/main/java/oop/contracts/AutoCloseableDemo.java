package oop.contracts;

// ANCHOR: autocloseable-demo
public final class AutoCloseableDemo {
  static final class Res implements AutoCloseable {
    private final String name;

    Res(String name) {
      this.name = name;
      System.out.println("open " + name);
    }

    void use() {
      System.out.println("use " + name);
    }

    @Override
    public void close() {
      System.out.println("close " + name);
    }
  }

  public static void main(String[] args) {
    try (Res a = new Res("A"); Res b = new Res("B")) {
      a.use();
      b.use();
    }
    System.out.println("---");
    try (Res a = new Res("A"); Res b = new Res("B")) {
      a.use();
      throw new IllegalStateException("boom");
    } catch (IllegalStateException e) {
      System.out.println("caught " + e.getMessage());
    } finally {
      System.out.println("finally");
    }
  }
}
// ANCHOR_END: autocloseable-demo
