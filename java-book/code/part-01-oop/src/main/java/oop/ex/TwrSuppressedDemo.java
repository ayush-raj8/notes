package oop.ex;

// ANCHOR: twr-suppressed
public final class TwrSuppressedDemo {
  static final class R implements AutoCloseable {
    private final String name;

    R(String name) {
      this.name = name;
    }

    @Override
    public void close() throws Exception {
      throw new Exception("close-" + name);
    }
  }

  public static void main(String[] args) {
    try (R a = new R("a"); R b = new R("b")) {
      throw new RuntimeException("body");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      for (Throwable s : e.getSuppressed()) {
        System.out.println("  suppressed " + s.getMessage());
      }
    }

    try (R a = new R("a"); R b = new R("b")) {
      System.out.println("body ok");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      for (Throwable s : e.getSuppressed()) {
        System.out.println("  suppressed " + s.getMessage());
      }
    }
  }
}
// ANCHOR_END: twr-suppressed
