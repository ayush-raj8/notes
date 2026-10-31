package oop.init;

// ANCHOR: static-block-demo
class LazyConfig {
  static int loads;

  static {
    System.out.println("LazyConfig static block");
    loads++;
  }

  LazyConfig() {
    System.out.println("LazyConfig ctor");
  }
}

public final class StaticBlockDemo {
  public static void main(String[] args) {
    System.out.println("main start");
    new LazyConfig();
    new LazyConfig();
    System.out.println("loads=" + LazyConfig.loads);
  }
}
// ANCHOR_END: static-block-demo
