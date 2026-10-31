package patterns.creational.singleton.exam;

// ANCHOR: predict-holder-lazy
public final class PredictHolderLazy {
  static final class Lazy {
    private Lazy() {
      System.out.println("ctor");
    }

    private static final class Holder {
      static final Lazy INSTANCE = new Lazy();
    }

    static Lazy get() {
      return Holder.INSTANCE;
    }

    static void hello() {
      System.out.println("hello");
    }
  }

  public static void main(String[] args) {
    System.out.println("start");
    Lazy.hello();
    System.out.println("after hello");
    Lazy.get();
    System.out.println("end");
  }
}
// ANCHOR_END: predict-holder-lazy
