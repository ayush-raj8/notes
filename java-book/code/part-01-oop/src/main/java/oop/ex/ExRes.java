package oop.ex;

// ANCHOR: ex-res
/** A resource that logs open/close and can fail on close. */
class ExRes implements AutoCloseable {
  private final String name;
  private final boolean failOnClose;

  ExRes(String name, boolean failOnClose) {
    this.name = name;
    this.failOnClose = failOnClose;
    System.out.println("open " + name);
  }

  @Override
  public void close() { // narrower than AutoCloseable.close() throws Exception: no throws needed
    System.out.println("close " + name);
    if (failOnClose) {
      throw new IllegalStateException("close-" + name);
    }
  }
}
// ANCHOR_END: ex-res
