package patterns.creational.singleton.exam;

// ANCHOR: predict-eager-timing
public final class PredictEagerTiming {
  static final class Eager {
    private static final Eager INSTANCE = new Eager();

    private Eager() {
      System.out.println("ctor");
    }

    static Eager get() {
      return INSTANCE;
    }

    static void hello() {
      System.out.println("hello");
    }
  }

  public static void main(String[] args) {
    System.out.println("start");
    Eager.hello();
    System.out.println("after hello");
    Eager.get();
    System.out.println("end");
  }
}
// ANCHOR_END: predict-eager-timing
