package oop.conc.solutions;

/** Exercise 4: lazy, lock-free singleton holding a value computed once. */
public final class SolutionLazyHolder {
  private final String config;

  private SolutionLazyHolder() {
    System.out.println("loading config");
    this.config = "prod";
  }

  private static final class Lazy {
    static final SolutionLazyHolder INSTANCE = new SolutionLazyHolder();
  }

  public static SolutionLazyHolder get() {
    return Lazy.INSTANCE;
  }

  public String config() {
    return config;
  }

  public static void main(String[] args) {
    System.out.println("before first get");
    System.out.println(get().config());
    System.out.println(get() == get());
  }
}
